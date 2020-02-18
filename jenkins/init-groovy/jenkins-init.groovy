// Reference: Code below was derived from 
// https://github.com/thbkrkr/jks/blob/master/init.groovy.d/1-create-admin-user.groovy
import jenkins.model.*
import hudson.security.*
import hudson.model.* 

// Reference: Code was derived from
// https://gist.github.com/ivan-pinatti/de063b610d1bdf2da229c7874968f4d9
import hudson.util.Secret
import java.nio.file.Files
import jenkins.model.Jenkins
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.*
import net.sf.json.JSONObject
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import com.cloudbees.plugins.credentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import hudson.plugins.sonar.*
import hudson.plugins.sonar.model.TriggersConfig
import hudson.tools.*


// Reference: Code below was derived from
// https://github.com/peterjenkins1/jenkins-scripts/blob/master/add-xml-job.groovy 
import com.cloudbees.hudson.plugins.folder.*


/////////////////////////
// Create Admin User
////////////////////////

// Reference: Code below was derived from 
// https://github.com/thbkrkr/jks/blob/master/init.groovy.d/1-create-admin-user.groovy

//get the environment variables to setup the admin account
def admin_username = System.getenv('ADMIN_USERNAME')
def admin_password = System.getenv('ADMIN_PASSWORD')

//create admin user with default username and password passed as enviroment variables
def securityRealm = new HudsonPrivateSecurityRealm(true)
securityRealm.createAccount(admin_username, admin_password)
Jenkins.instance.setSecurityRealm(securityRealm)

// configure jenkins to prevent unauthorized access
def authorizaton_strat = new FullControlOnceLoggedInAuthorizationStrategy()
authorizaton_strat.setAllowAnonymousRead(false)
Jenkins.instance.setAuthorizationStrategy(authorizaton_strat)
Jenkins.instance.save()

/////////////////////////
// Create Credentials
////////////////////////

// Reference: Code was derived from
// https://gist.github.com/ivan-pinatti/de063b610d1bdf2da229c7874968f4d9

def privateKey = new File('/usr/share/jenkins/.ssh/private_key').text

Jenkins jenkins = Jenkins.getInstance()
def domain = Domain.global()
def store = jenkins.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

def deployKey = new BasicSSHUserPrivateKey(
  CredentialsScope.GLOBAL,
  'deployment-key',
  'Gitlab_Deployment_Key',
  new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(privateKey),
  "",
  'Deployment SSH Key'
) 
store.addCredentials(domain, deployKey)
jenkins.save()

def sonar_token = new File("/var/jenkins_home/sonar_token.txt").text

// inject sonar token
sonarSecretText = new StringCredentialsImpl(
    CredentialsScope.GLOBAL,
    "sonar-token",
    "Token for sonarqube",
    Secret.fromString(sonar_token)
    )

store.addCredentials(domain, sonarSecretText) 
jenkins.save()

def projectName = "COMP370_SafeHold_Build"
def job = new File("/usr/share/jenkins/ref/init.groovy.d/backend_pipeline.xml").text

def xmlBuffer = new StringBufferInputStream(job)
job = jenkins.createProjectFromXML(projectName, xmlBuffer)

jenkins.save()


// define Bitbucket secret
def sonarqube_Credentials = new UsernamePasswordCredentialsImpl(
  CredentialsScope.GLOBAL,
  "sonarqube_credentials",
  "Credentials for SonarQube",
  System.getenv('SONAR_USER_LOGIN'),
  System.getenv('SONAR_USER_PASSWORD')
)

// add credential to store
store.addCredentials(domain, sonarqube_Credentials)
// save to disk
jenkins.save()


// Code below was derived from:
// https://stackoverflow.com/questions/49794536/groovy-script-to-set-sonarqube-server-settings-in-jenkins-programatically


// Required environment variables
def sonar_name = "sonar"
def sonar_server_url = "http://sonar:9000"
def sonar_auth_token = "sonar-token"
def sonar_mojo_version = ''
def sonar_additional_properties = ''
def sonar_triggers = new TriggersConfig()
def sonar_additional_analysis_properties = ''
def sonar_runner_version = '4.2.0.1873'
Thread.start {

    sleep(10000)
    println("Configuring SonarQube...")

    // Get the GlobalConfiguration descriptor of SonarQube plugin.
    def SonarGlobalConfiguration sonar_conf = jenkins.getDescriptor(SonarGlobalConfiguration.class)

    def sonar_inst = new SonarInstallation(
        sonar_name,
        sonar_server_url,
        "sonar-token",
        sonarSecretText.getSecret(),
        "",
        sonar_mojo_version,
        sonar_additional_properties,
        sonar_additional_analysis_properties,
        sonar_triggers
    ) 
    // Only add the new Sonar setting if it does not exist - do not overwrite existing config
    def sonar_installations = sonar_conf.getInstallations()
    def sonar_inst_exists = false
    sonar_installations.each {
        installation = (SonarInstallation) it
        if (sonar_inst.getName() == installation.getName()) {
            sonar_inst_exists = true
            println("Found existing installation: " + installation.getName())
        }
    }
    if (!sonar_inst_exists) {
        sonar_installations += sonar_inst
        sonar_conf.setInstallations((SonarInstallation[]) sonar_installations)
        sonar_conf.save()
    }

    // Step 2 - Configure SonarRunner
    println("Configuring SonarRunner...")
    def desc_SonarRunnerInst = jenkins.getDescriptor("hudson.plugins.sonar.SonarRunnerInstallation")
    def sonarRunnerInstaller = new SonarRunnerInstaller(sonar_runner_version)
    def installSourceProperty = new InstallSourceProperty([sonarRunnerInstaller])
    def sonarRunner_inst = new SonarRunnerInstallation("sonarscanner", "", [installSourceProperty])

    // Only add our Sonar Runner if it does not exist - do not overwrite existing config
    def sonar_runner_installations = desc_SonarRunnerInst.getInstallations()
    def sonar_runner_inst_exists = false
    sonar_runner_installations.each {
        installation = (SonarRunnerInstallation) it
        if (sonarRunner_inst.getName() == installation.getName()) {
            sonar_runner_inst_exists = true
            println("Found existing installation: " + installation.getName())
        }
    }
    if (!sonar_runner_inst_exists) {
        sonar_runner_installations += sonarRunner_inst
        desc_SonarRunnerInst.setInstallations((SonarRunnerInstallation[]) sonar_runner_installations)
        desc_SonarRunnerInst.save()
    }
    

    // Save the state
    jenkins.save()
}