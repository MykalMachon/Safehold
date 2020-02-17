FROM jenkins/jenkins:latest

# in case servers are down uncomment the lines below, else keep them commented
ENV CURL_OPTIONS -sSfLk -C -
ENV JENKINS_UC_DOWNLOAD http://ftp-nyc.osuosl.org/pub/jenkins
RUN JENKINS_UC_DOWNLOAD=http://ftp-nyc.osuosl.org/pub/jenkins

ADD ./jenkins_setup.sh /jenkins_setup.sh
RUN /usr/local/bin/install-plugins.sh git ssh github-branch-source sonar jdk-tool gradle ssh-slaves ssh-agent generic-webhook-trigger credentials-binding gitlab-plugin workflow-aggregator matrix-auth docker-workflow blueocean python
# skip config 
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

ENV ADMIN_USERNAME admin
ENV ADMIN_PASSWORD admin

USER root
RUN curl -sL https://deb.nodesource.com/setup_13.x | bash -
RUN apt-get update
RUN apt-get install nodejs -y
 

ADD ./deployment-keys/ /usr/share/jenkins/.ssh/ 
ADD ./deployment-keys/ /var/jenkins_home/.ssh/ 
ADD ./init-groovy/ /usr/share/jenkins/ref/init.groovy.d/  
 
# Authorize SSH Host
RUN ssh-keyscan cisgitlab.ufv.ca >  /usr/share/jenkins/.ssh/known_hosts
RUN ssh-keyscan cisgitlab.ufv.ca >  /var/jenkins_home/.ssh/known_hosts


RUN chown jenkins /jenkins_setup.sh
RUN chown -R jenkins /usr/share/jenkins/ref/init.groovy.d 
RUN chown -R jenkins /usr/share/jenkins/.ssh/ 


RUN chmod 777 /jenkins_setup.sh
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

# https://sketchingdev.co.uk/blog/continuous-integration-for-android-with-jenkins-docker-and-aws.html 
# Install Deps
# RUN dpkg --add-architecture i386 && \
#     apt-get update && \
#     apt-get install -y --force-yes expect git wget libc6-i386 lib32stdc++6 lib32gcc1 lib32ncurses5 lib32z1 python curl && \
#     apt-get clean
# 
# # Install Android SDK
# ENV ANDROID_SDK_VERSION=r29.0.5 \
#     ANDROID_BUILD_TOOLS_VERSION=29.0.3 \
#     ANDROID_API_LEVELS=android-29
# 
# RUN cd /opt &&\
#     wget --output-document=sdk-tools.zip --quiet https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip && \
#     wget --output-document=sdk-platform-tools.zip --quiet https://dl.google.com/android/repository/platform-tools_r29.0.5-linux.zip && \
#     unzip sdk-tools.zip -d android-sdk && \
#     unzip sdk-platform-tools.zip -d android-sdk && \
#     rm -f sdk-tools.zip && \
#     rm -f sdk-platform-tools.zip && \
#     chown -R root:root android-sdk
# 
# ENV ANDROID_HOME /opt/android-sdk
# ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools
# 
# RUN echo y | android update sdk --no-ui -a --filter tools,platform-tools,${ANDROID_API_LEVELS},build-tools-${ANDROID_BUILD_TOOLS_VERSION},extra-android-support,extra-android-m2repository,extra-google-m2repository
# RUN echo y | android update sdk --no-ui --all --filter android-29
# RUN echo y | android update sdk --no-ui --all --filter build-tools-29.0.3
# 
# 
# RUN echo y | sdkmanager platform-tools "build-tools;29.0.2"
# RUN echo y | sdkmanager platform-tools "platforms;android-29"
# RUN echo y | sdkmanager platform-tools "build-tools;28.0.3"
# RUN echo y | sdkmanager platform-tools "platforms;android-28"
# RUN echo y | sdkmanager platform-tools "build-tools;27.0.3"
# RUN echo y | sdkmanager platform-tools "platforms;android-27"
# RUN echo y | sdkmanager platform-tools "build-tools;26.0.3"
# RUN echo y | sdkmanager platform-tools "platforms;android-26"
# RUN echo y | sdkmanager platform-tools "build-tools;25.0.3"
# RUN echo y | sdkmanager platform-tools "platforms;android-25"
# RUN echo y | sdkmanager platform-tools "build-tools;23.0.3"
# RUN echo y | sdkmanager platform-tools "platforms;android-23"
# RUN echo y | sdkmanager platform-tools "extras;android;m2repository"


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
RUN chown jenkins:jenkins /jenkins_setup.sh
USER jenkins
ENTRYPOINT ["/bin/bash", "/jenkins_setup.sh"]
# sonar 62f5e24a659e4d749b1743a442367e3f8cd8db12
