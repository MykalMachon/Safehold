FROM jenkins/jenkins:latest
RUN /usr/local/bin/install-plugins.sh git ssh github-branch-source sonar jdk-tool gradle ssh-slaves ssh-agent generic-webhook-trigger credentials-binding gitlab-plugin workflow-aggregator matrix-auth docker-workflow blueocean python
# skip config 
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

ENV ADMIN_USERNAME admin
ENV ADMIN_PASSWORD admin

# TODO: replace the keys

USER jenkins




USER root
ADD ./deployment-keys/ /usr/share/jenkins/.ssh/ 
ADD ./deployment-keys/ /var/jenkins_home/.ssh/ 
ADD ./init-groovy/ /usr/share/jenkins/ref/init.groovy.d/  
 
# Authorize SSH Host
RUN ssh-keyscan cisgitlab.ufv.ca >  /usr/share/jenkins/.ssh/known_hosts
RUN ssh-keyscan cisgitlab.ufv.ca >  /var/jenkins_home/.ssh/known_hosts


ADD ./jenkins_setup.sh /jenkins_setup.sh
RUN chown jenkins /jenkins_setup.sh
RUN chown -R jenkins /usr/share/jenkins/ref/init.groovy.d 
RUN chown -R jenkins /usr/share/jenkins/.ssh/ 


RUN chmod +x /jenkins_setup.sh
RUN apt-get update 

RUN curl https://cli-assets.heroku.com/install-ubuntu.sh | sh


RUN apt-get update -qq && apt-get install -qqy apt-transport-https ca-certificates curl gnupg2 software-properties-common 
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
RUN apt-get update  -qq && apt-get install docker-ce=17.12.1~ce-0~debian -y 
RUN usermod -a -G root jenkins

#USER jenkins

CMD DOCKER_GID=$(stat -c '%g' /var/run/docker.sock) && \
    groupadd -for -g ${DOCKER_GID} docker && \
    usermod -aG docker jenkins 

USER jenkins
ENTRYPOINT ["/jenkins_setup.sh"]
# sonar 62f5e24a659e4d749b1743a442367e3f8cd8db12
