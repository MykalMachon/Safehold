FROM jenkins/jenkins:latest

# in case servers are down uncomment the lines below, else keep them commented
ENV CURL_OPTIONS -sSfLk -C -
ENV JENKINS_UC_DOWNLOAD http://ftp-nyc.osuosl.org/pub/jenkins
RUN JENKINS_UC_DOWNLOAD=http://ftp-nyc.osuosl.org/pub/jenkins

RUN /usr/local/bin/install-plugins.sh git ssh github-branch-source sonar android-lint jacoco jdk-tool gradle ssh-slaves ssh-agent generic-webhook-trigger credentials-binding gitlab-plugin workflow-aggregator matrix-auth docker-workflow blueocean python
# skip config 
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

ENV ADMIN_USERNAME admin
ENV ADMIN_PASSWORD admin

USER jenkins
ADD --chown=jenkins:jenkins ./deployment-keys/ /usr/share/jenkins/.ssh/ 
ADD --chown=jenkins:jenkins ./deployment-keys/ /var/jenkins_home/.ssh/ 
ADD --chown=jenkins:jenkins ./init-groovy/ /usr/share/jenkins/ref/init.groovy.d/  
ADD --chown=jenkins:jenkins ./jenkins_setup.sh /jenkins_setup.sh 
 
RUN chmod +x /jenkins_setup.sh

# Authorize SSH Host
RUN ssh-keyscan cisgitlab.ufv.ca >  /usr/share/jenkins/.ssh/known_hosts
RUN ssh-keyscan cisgitlab.ufv.ca >  /var/jenkins_home/.ssh/known_hosts


USER root
RUN curl -sL https://deb.nodesource.com/setup_13.x | bash -
RUN apt-get update
RUN apt-get install nodejs -y
RUN apt-get update 
RUN curl https://cli-assets.heroku.com/install-ubuntu.sh | sh

RUN apt-get update -qq && apt-get install -qqy apt-transport-https ca-certificates curl gnupg2 software-properties-common 
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
RUN apt-get update  -qq && apt-get install docker-ce=17.12.1~ce-0~debian -y  
RUN apt-get install jq -y
RUN usermod -a -G root jenkins

CMD DOCKER_GID=$(stat -c '%g' /var/run/docker.sock) && \
    groupadd -for -g ${DOCKER_GID} docker && \
    usermod -aG docker jenkins 

USER jenkins
ENTRYPOINT ["/jenkins_setup.sh"]