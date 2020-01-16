FROM jenkins/jenkins
RUN /usr/local/bin/install-plugins.sh git ssh github-branch-source sonar jdk-tool gradle ssh-slaves ssh-agent generic-webhook-trigger credentials-binding gitlab-plugin workflow-aggregator matrix-auth docker-workflow blueocean python
# skip config 
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

ENV ADMIN_USERNAME admin
ENV ADMIN_PASSWORD admin

COPY ./init-groovy/ /usr/share/jenkins/ref/init.groovy.d/

# TODO: replace the keys

COPY ./deployment-keys/ /usr/share/jenkins/.ssh/
COPY ./deployment-keys/ /var/jenkins_home/.ssh/ 

USER root
# Authorize SSH Host
RUN ssh-keyscan cisgitlab.ufv.ca >  /usr/share/jenkins/.ssh/known_hosts
RUN ssh-keyscan cisgitlab.ufv.ca >  /var/jenkins_home/.ssh/known_hosts



ADD ./jenkins_setup.sh /jenkins_setup.sh
RUN chown jenkins /jenkins_setup.sh
RUN chmod +x /jenkins_setup.sh
RUN apt-get update
RUN apt-get install -y jq

USER jenkins

ENTRYPOINT ["/jenkins_setup.sh"]
# sonar 62f5e24a659e4d749b1743a442367e3f8cd8db12
