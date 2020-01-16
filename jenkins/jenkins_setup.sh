#!/bin/bash
#
# This file was derived from: https://github.com/linagora/docker-sonarqube-pr/blob/master/start_with_profile.sh

function curlAdmin {
    curl -u $SONAR_USER_LOGIN:$SONAR_USER_PASSWORD $@
}

BASE_URL=http://sonar:9000

function isUp {
    curl -s -u $SONAR_USER_LOGIN:$SONAR_USER_PASSWORD -f "$BASE_URL/api/system/info"
}

# Wait for server to be up
PING=`isUp`
while [ -z "$PING" ]
do
    sleep 15
    PING=`isUp`
done


# Provision a project
curlAdmin -X POST "$BASE_URL/api/user_tokens/generate?name=jenkins-token" 2>/dev/null | jq -r '.token' > /var/jenkins_home/sonar_token.txt
# cat /var/jenkins_home/sonar_token.txt

# Start Jenkins 
/sbin/tini "--" "/usr/local/bin/jenkins.sh" &

wait