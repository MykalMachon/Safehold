#!/bin/bash 
#
# This file was derived from: https://github.com/linagora/docker-sonarqube-pr/blob/master/start_with_profile.sh
function curlAdmin {
    curl -v -u admin:admin $@
}

BASE_URL=$SONAR_URL

function isUp {
    curl -s -u admin:admin -f "$BASE_URL/api/system/info"
}

# Wait for server to be up
PING=`isUp`
while [ -z "$PING" ]
do
    sleep 5
    echo "sleeping...waiting for sonar..."
    PING=`isUp`
done

curlAdmin -X POST "$BASE_URL/api/webhooks/create?name=jenkins&url=$JENKINS_URL/sonarqube-webhook/" 

# Create Project PR_Backend, master_backend, PR_Client and  master_Client
if [ "$SONAR_BACKEND_PROJECT_NAME" ] && [ "$SONAR_BACKEND_PROJECT_KEY" ]; then
    curlAdmin -X POST "$BASE_URL/api/projects/create?name=master_$SONAR_BACKEND_PROJECT_NAME&key=master_$SONAR_BACKEND_PROJECT_KEY"
    curlAdmin -X POST "$BASE_URL/api/projects/create?name=PR_$SONAR_BACKEND_PROJECT_NAME&key=PR_$SONAR_BACKEND_PROJECT_KEY"

fi

if [ "$SONAR_CLIENT_PROJECT_NAME" ] && [ "$SONAR_CLIENT_PROJECT_KEY" ]; then
    curlAdmin -X POST "$BASE_URL/api/projects/create?name=master_$SONAR_CLIENT_PROJECT_NAME&key=master_$SONAR_CLIENT_PROJECT_KEY"
    curlAdmin -X POST "$BASE_URL/api/projects/create?name=PR_$SONAR_CLIENT_PROJECT_NAME&key=PR_$SONAR_CLIENT_PROJECT_KEY"
fi


# Configure an Admin user using environment vars
if [ "$SONAR_USER_LOGIN" ] && [ "$SONAR_USER_NAME" ] && [ "$SONAR_USER_PASSWORD" ]; then
    curlAdmin -X POST "$BASE_URL/api/users/create?login=$SONAR_USER_LOGIN&name=$SONAR_USER_NAME&password=$SONAR_USER_PASSWORD"
    curlAdmin -X POST "$BASE_URL/api/permissions/add_user?login=$SONAR_USER_LOGIN&permission=admin"
    curl -v -u $SONAR_USER_LOGIN:$SONAR_USER_PASSWORD -X POST "$BASE_URL/api/users/deactivate?login=admin"
fi

function curlAdminNewUser {
    curl -u $SONAR_USER_LOGIN:$SONAR_USER_PASSWORD $@
}

# Provision a project
curlAdminNewUser -X POST "$BASE_URL/api/user_tokens/generate?name=jenkins-token" 2>/dev/null | jq -r '.token' >> /var/jenkins_home/sonar_token.txt
# cat /var/jenkins_home/sonar_token.txt

# Start Jenkins 
/sbin/tini "--" "/usr/local/bin/jenkins.sh" &

wait