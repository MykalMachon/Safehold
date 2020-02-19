def SONAR_URL= ""
//docker for build
pipeline {
   agent any 

   environment {
     backend_folder = "./backend"
     SONAR_CREDS = credentials('sonarqube_credentials')
     SONAR_BACKEND_PROJECT_TOKEN_MASTER = "master_safehold_backend"
   }

   stages {
        stage('Checkout Code') {
             steps { 
                git branch: "${env.BRANCH_NAME}", changelog: false, credentialsId: 'deployment-key', url: 'git@cisgitlab.ufv.ca:arshsekhon/comp_370_project.git'
                stash name: 'repo_stash'
                script{
                    SONAR_URL = sh script:'echo -n $SONAR_URL', returnStdout: true 
                } 
             }
        }
        
        stage('Backend: Install Dependencies') {
        agent { 
               docker { 
                   image 'node:12.16' 
                    args '-u root:root' 
               }  
           }
          steps { 
            deleteDir()
            script {
              unstash 'repo_stash'
              sh '''pwd;
                    ls -al;
                    cd ./backend/ ;  
                    npm install;
                    chmod -R 777 ./;'''  
              stash name: 'repo_stash'
            }
            
          }
          post {
                  always {
                      sh 'chmod -R 0777 *;'
                  }
              } 
        }
        stage('Backend: Run Tests') {
            
            agent { 
               docker { 
                   image 'node:12.16' 
                    args '-u root:root' 
               }  
           }
          steps { 
            deleteDir()
            script {
              unstash 'repo_stash'
              sh '''cd ./backend/; 
                    npm run test-coverage;
                    ls ./coverage
                    chmod -R 777 .;'''     
              stash name: 'repo_stash'
            }
            
          }
          post {
                always { 
                  junit testResults:'**/coverage/junit/**.xml'  
                  stash name: 'repo_stash'
                }
          }
        } 
        
        
        stage('Backend: SonarQube Analysis') {
         agent any
         steps { 
             deleteDir()
             script{ 

                 def sonar_project_token = ""
                 if (env.BRANCH_NAME == 'master')
                    sonar_project_token = "${SONAR_BACKEND_PROJECT_TOKEN_MASTER}" 
                 else 
                    sonar_project_token= env.BRANCH_NAME + "_safehold_backend";
                 unstash 'repo_stash' 
                 def scannerHome = tool 'sonarscanner';
                 withSonarQubeEnv('sonar') {
                     sh """${scannerHome}/bin/sonar-scanner \
                     -Dsonar.login=${SONAR_CREDS_USR} \
                     -Dsonar.password=${SONAR_CREDS_PSW} \
                     -Dsonar.projectKey=${sonar_project_token} \
                     -Dsonar.sources=./backend \
                     -Dsonar.exclusions=**/__tests__/**,**/coverage/**,**/node_modules/**\
                     -Dsonar.coverage.exclusions=**/__tests__/** \
                     -Dsonar.javascript.lcov.reportPaths=./backend/coverage/jest/lcov.info \
                     """
                }
            }
             
         }
         post {
                  always {
                      sh 'chmod -R 0777 *;'
                  }
              }
          
      }
      stage("Backend: Quality Gate") {
            steps {
              timeout(time: 1, unit: 'HOURS') {
                waitForQualityGate abortPipeline: true
              }
            }
          }
       
      stage('Android: Build'){
          agent{
              dockerfile {
                  dir 'android'
                  args '-u root:root --tmpfs /.config'  
                  reuseNode true
              } 
          }
          steps{  
              deleteDir()
              unstash 'repo_stash'
              sh '''
                  cd android; 
                  gradle wrapper; 
                    ./gradlew clean assembleDebug
                      chmod -R 0777 ../*; '''
              archive '.android/app/build/outputs/**/app-debug.apk'
              //stash name: 'repo_stash'
          }
          post {
                  always {
                      sh 'chmod -R 0777 *;'
                  }
              }
      }
      
      
      
      
        stage('Android: Running Tests, Sonar Analysis and Lint'){
          agent{
              dockerfile {
                  dir 'android'
                  args '-u root:root --tmpfs /.config --network jenkins_default' 
                  reuseNode true
              } 
          }
          steps{ 
                sh '''cd ./android;
                      gradle wrapper; 
                      ./gradlew lint;
                      chmod -R 0777 ../*;
                      ls -al ./app/build/reports/lint-results.xml
'''             
              sh 'ls -al ./android/app/build/reports/lint-results.xml'
              stash includes: '**/android/app/build/reports/lint-results*', name: 'lint-reports'
            
      
          script{
                    try {
                        sh '''cd ./android;
                              gradle wrapper; 
                              ./gradlew test;
                              ./gradlew codeCoverageReport;
                              chmod -R 0777 ../*;
                              ls -al ./app/build'''
                    } catch (err) {
                        currentBuild.result = 'UNSTABLE'
                    }
                
                    stash includes: '**/test-results/**/*.xml', name: 'junit-reports'
                    stash includes: '**/jacoco/**', name: 'jacoco-coverage-reports'
                    
                    def sonar_android_project_token = ""
                    sonar_android_project_token= env.BRANCH_NAME + "_safehold_android_app";
                    sh """ cd ./android;
                          ./gradlew :app:assembleDebug
                          ./gradlew :app:assembleDebugAndroidTest;
                          chmod -R 0777 ../*;
                          ./gradlew clean test codeCoverageReport sonarqube \
                          -Dsonar.host.url=${SONAR_URL} \
                          -Dsonar.projectKey=${sonar_android_project_token} \
                          -Dsonar.projectName=${sonar_android_project_token} --info"""
                    
                  sh 'chmod -R 0777 *;'
                          
                } 
                  archive 'app/build/outputs/**/*androidTest*.apk'
                  
                  
                  jacoco( 
                        execPattern: '**/android/app/*.exec',
                        classPattern: '**/android/app/**/classes',
                        sourcePattern: './android/app/src/main/java',
                        exclusionPattern: './android/app/src/test*'
                  )
                  
                  
              }

              post {
                  always {
                      sh 'chmod -R 0777 *;'
                  }
              }
              
          } 
      
          stage('Android: Publishing Results to Jenkins'){
              agent any
              steps{
                  unstash 'junit-reports'
                  sh 'ls -al .'
                  sh 'ls -al ./android/app/build'
                  junit testResults:'**/test-results/**/*.xml'   
                  unstash 'lint-reports'
                  step([$class: 'LintPublisher', canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: '**/app/build/reports/lint-results*.xml', unHealthy: ''])
                    
                  
              }
              post {
                  always {
                      sh 'chmod -R 0777 *;'
                  }
              }
          }

      stage('Backend: Deploy to Production') {
          agent any
          when {
          expression {
              return env.BRANCH_NAME == 'master';
              }
          }
          steps {
              echo 'Deployment to the Heroku bash goes here'
          }
          post {
                  always {
                      sh 'chmod -R 0777 *;'
                  }
              }
      }
         
   }
   
}