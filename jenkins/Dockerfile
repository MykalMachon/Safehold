FROM jenkins/jenkins:latest

# in case servers are down uncomment the lines below, else keep them commented
ENV CURL_OPTIONS -sSfLk -C -
ENV JENKINS_UC_DOWNLOAD http://ftp-nyc.osuosl.org/pub/jenkins
RUN JENKINS_UC_DOWNLOAD=http://ftp-nyc.osuosl.org/pub/jenkins

RUN /usr/local/bin/install-plugins.sh git ssh github-branch-source sonar jdk-tool gradle ssh-slaves ssh-agent generic-webhook-trigger credentials-binding gitlab-plugin workflow-aggregator matrix-auth docker-workflow blueocean python

# skip config 
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

ENV ADMIN_USERNAME admin
ENV ADMIN_PASSWORD admin

USER root
RUN curl -sL https://deb.nodesource.com/setup_13.x | bash -
RUN apt-get update
RUN apt-get install nodejs -y
 
USER jenkins
ADD --chown=jenkins:jenkins ./deployment-keys/ /usr/share/jenkins/.ssh/ 
ADD --chown=jenkins:jenkins ./deployment-keys/ /var/jenkins_home/.ssh/ 
ADD --chown=jenkins:jenkins ./init-groovy/ /usr/share/jenkins/ref/init.groovy.d/ 
ADD --chown=jenkins:jenkins ./jenkins_setup.sh /jenkins_setup.sh


RUN chmod 777 /jenkins_setup.sh

USER root
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


# image script obtained from https://github.com/WindSekirun/Jenkins-Android-Docker/blob/master/Dockerfile
ENV ANDROID_SDK_ZIP sdk-tools-linux-4333796.zip
ENV ANDROID_SDK_ZIP_URL https://dl.google.com/android/repository/$ANDROID_SDK_ZIP
ENV ANDROID_HOME /opt/android-sdk-linux

ENV GRADLE_ZIP gradle-5.4.1-bin.zip
ENV GRADLE_ZIP_URL https://services.gradle.org/distributions/$GRADLE_ZIP

ENV PATH $PATH:$ANDROID_HOME/tools/bin
ENV PATH $PATH:$ANDROID_HOME/platform-tools
ENV PATH $PATH:/opt/gradle-5.4.1/bin

# Build-time metadata as defined at http://label-schema.org
ARG BUILD_DATE
ARG VCS_REF
ARG VERSION
LABEL org.label-schema.build-date=$BUILD_DATE \
          org.label-schema.name="Jenkins-Android-Docker" \
          org.label-schema.description="Docker image for Jenkins with Android " \
          org.label-schema.vcs-ref=$VCS_REF \
          org.label-schema.vcs-url="https://github.com/WindSekirun/Jenkins-Android-Docker" \
          org.label-schema.vendor="WindSekirun" \
          org.label-schema.version=$VERSION \
          org.label-schema.schema-version="1.0"

USER root

RUN mkdir -p ~/.android && touch ~/.android/repositories.cfg
## Install requirements
RUN dpkg --add-architecture i386
RUN rm -rf /var/lib/apt/list/* && apt-get update && apt-get install jq ca-certificates curl gnupg2 software-properties-common git unzip file apt-utils lxc apt-transport-https -y
 
## Install Android SDK into Image
RUN wget --output-document=/opt/$GRADLE_ZIP $GRADLE_ZIP_URL 
RUN unzip /opt/$GRADLE_ZIP -d /opt/ && rm /opt/$GRADLE_ZIP

RUN wget --output-document=/opt/$ANDROID_SDK_ZIP $ANDROID_SDK_ZIP_URL
RUN unzip -q /opt/$ANDROID_SDK_ZIP -d $ANDROID_HOME && rm /opt/$ANDROID_SDK_ZIP

RUN echo y | sdkmanager platform-tools "build-tools;29.0.3"
RUN echo y | sdkmanager platform-tools "platforms;android-29"
RUN echo y | sdkmanager platform-tools "build-tools;28.0.3"
RUN echo y | sdkmanager platform-tools "platforms;android-28"
RUN echo y | sdkmanager platform-tools "build-tools;27.0.3"
RUN echo y | sdkmanager platform-tools "platforms;android-27"
RUN echo y | sdkmanager platform-tools "build-tools;26.0.3"
RUN echo y | sdkmanager platform-tools "platforms;android-26"
RUN echo y | sdkmanager platform-tools "build-tools;25.0.3"
RUN echo y | sdkmanager platform-tools "platforms;android-25"
RUN echo y | sdkmanager platform-tools "build-tools;23.0.3"
RUN echo y | sdkmanager platform-tools "platforms;android-23"
RUN echo y | sdkmanager platform-tools "extras;android;m2repository"
RUN chown -R jenkins $ANDROID_HOME

RUN apt-get install libc6:i386 libncurses5:i386 libstdc++6:i386 zlib1g:i386 -y --no-install-recommends

RUN apt-get clean
RUN rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN ls -al 

USER jenkins
ENTRYPOINT ["/bin/bash", "/jenkins_setup.sh"]
# sonar 62f5e24a659e4d749b1743a442367e3f8cd8db12
