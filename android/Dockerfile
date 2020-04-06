FROM ubuntu:bionic

# image script obtained from https://github.com/WindSekirun/Jenkins-Android-Docker/blob/master/Dockerfile
ENV ANDROID_SDK_ZIP sdk-tools-linux-4333796.zip
ENV ANDROID_SDK_ZIP_URL https://dl.google.com/android/repository/$ANDROID_SDK_ZIP
ENV ANDROID_HOME /opt/android-sdk-linux

ENV GRADLE_ZIP gradle-5.6.4.zip
ENV GRADLE_ZIP_URL https://services.gradle.org/distributions/$GRADLE_ZIP

ENV PATH $PATH:$ANDROID_HOME/tools/bin
ENV PATH $PATH:$ANDROID_HOME/platform-tools
ENV PATH $PATH:/opt/gradle-5.6.4/bin


USER root

# This is in accordance to : https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04
RUN apt-get update && \
	apt-get install -y openjdk-8-jdk && \
	apt-get install -y ant && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk8-installer;
	
# Fix certificate issues, found as of 
# https://bugs.launchpad.net/ubuntu/+source/ca-certificates-java/+bug/983302
RUN apt-get update && \
	apt-get install -y ca-certificates-java && \
	apt-get clean && \
	update-ca-certificates -f && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk8-installer;

# Setup JAVA_HOME, this is useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME
     
RUN mkdir -p ~/.android && touch ~/.android/repositories.cfg
## Install requirements
RUN dpkg --add-architecture i386
RUN rm -rf /var/lib/apt/list/* && apt-get update && apt-get install wget jq ca-certificates curl gnupg2 software-properties-common git unzip file apt-utils lxc apt-transport-https -y
 
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

RUN apt-get install libc6:i386 libncurses5:i386 libstdc++6:i386 zlib1g:i386 -y --no-install-recommends

RUN apt-get clean
RUN rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN ls -al 