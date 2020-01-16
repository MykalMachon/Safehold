FROM sonarqube:latest
USER root
RUN apt-get update
RUN apt-get -y install wget
RUN wget -P /opt/sonarqube/extensions/plugins/ https://dl.bintray.com/sonarsource/SonarQube/org%2Fsonarsource%2Fpython%2Fsonar-python-plugin%2F2.2.0.5331%2Fsonar-python-plugin-2.2.0.5331.jar -O sonar-python-plugin-2.2.0.5331.jar
RUN wget -P /opt/sonarqube/extensions/plugins/ https://dl.bintray.com/sonarsource/SonarQube/org%2Fsonarsource%2Fjava%2Fsonar-java-plugin%2F6.0.1.20589%2Fsonar-java-plugin-6.0.1.20589.jar -O sonar-java-plugin-6.0.1.20589.jar
RUN chown -R sonarqube /opt/sonarqube/extensions/plugins/


ADD setup.sh /opt/sonarqube/setup.sh
RUN chown sonarqube /opt/sonarqube/setup.sh
RUN chmod +x /opt/sonarqube/setup.sh

USER sonarqube

ENV SONAR_BACKEND_PROJECT_NAME comp370_backend
ENV SONAR_BACKEND_PROJECT_KEY comp370_backend

ENV SONAR_CLIENT_PROJECT_NAME comp370_android
ENV SONAR_CLIENT_PROJECT_KEY comp370_android

ENTRYPOINT ["/opt/sonarqube/setup.sh"]