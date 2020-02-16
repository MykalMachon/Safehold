//docker for build
pipeline {
   agent any
   
   environment {
     backend_folder = "./backend"
     SONAR_CREDS = credentials('sonarqube_credentials')
     SONAR_BACKEND_PROJECT_TOKEN_MASTER = "master_comp370_backend"
   }

   stages {
        stage('Checkout Code') {
             steps { 
                git branch: "${env.BRANCH_NAME}", changelog: false, credentialsId: 'deployment-key', url: 'git@cisgitlab.ufv.ca:arshsekhon/comp_370_project.git'
                stash name: 'backend_stash'
             }
        }
        
        stage('Install Dependencies') {
        agent { 
               docker { 
                   image 'node:12.16' 
                    args '-u root:root' 
               }  
           }
          steps { 
            deleteDir()
            script {
              unstash 'backend_stash'
              sh '''pwd;
                    ls -al;
                    cd ./backend/ ;  
                    npm install;
                    chmod -R 777 ./;'''  
              stash name: 'backend_stash'
            }
            
          } 
        }
        stage('Run Tests') {
            
            agent { 
               docker { 
                   image 'node:12.16' 
                    args '-u root:root' 
               }  
           }
          steps { 
            deleteDir()
            script {
              unstash 'backend_stash'
              sh '''cd ./backend/; 
                    npm run test-coverage;
                    ls ./coverage
                    chmod -R 777 .;'''     
              stash name: 'backend_stash'
            }
            
          }
          post {
                always { 
                  junit testResults:'**/coverage/junit/**.xml'  
                  stash name: 'backend_stash'
                }
          }
        } 
        
        
        stage('SonarQube Analysis') {
         agent any
         steps { 
             deleteDir()
             script{ 

                 def sonar_project_token = ""
                 if (env.BRANCH_NAME == 'master')
                    sonar_project_token = env.SONAR_BACKEND_PROJECT_TOKEN_MASTER 
                 else 
                    sonar_project_token= env.BRANCH_NAME + "_comp370_backend";
                 unstash 'backend_stash' 
                 def scannerHome = tool 'sonarscanner';
                 withSonarQubeEnv('sonar') {
                     sh """${scannerHome}/bin/sonar-scanner \
                     -Dsonar.login=${SONAR_CREDS_USR} \
                     -Dsonar.password=${SONAR_CREDS_PSW} \
                     -Dsonar.projectKey=${SONAR_BACKEND_PROJECT_TOKEN} \
                     -Dsonar.sources=./backend \
                     -Dsonar.exclusions=**/__tests__/**,**/coverage/**,**/node_modules/**\
                     -Dsonar.coverage.exclusions=**/__tests__/** \
                     -Dsonar.javascript.lcov.reportPaths=./backend/coverage/jest/lcov.info \
                     """
                }
            }
             
         }
          
      }
      stage("Quality Gate") {
            steps {
              timeout(time: 1, unit: 'HOURS') {
                waitForQualityGate abortPipeline: true
              }
            }
          }

      stage('Deploy to Production') {
          agent label:'master'
          when {
          expression {
              return env.BRANCH_NAME == 'master';
              }
          }
          steps {
              echo 'Deployment to the Heroku bash goes here'
          }
      }
         
   }
   
}