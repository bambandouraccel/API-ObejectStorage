pipeline {
  agent any
  environment {
        APP_NAME= "objectStorage"
        OPENSHIFT_PROJECT = 'devops'
        IMAGE_NAME= "object_storage_api"
        DOCKER_USER= "mina0423"
        OC_SERVER= "https://api.ocp.heritage.africa:6443"
    }



  stages {

    stage('Checkout repo database') {
       steps {
          dir('mongodb-database-for-objectStorage') {
            git credentialsId: 'ssh-jenkins',
               url: 'https://github.com/bambandouraccel/mongodb-database-for-objectStorage.git',
               branch: 'main'
          }
       }
    }
    stage('Start Database') {
       steps {
         dir('mongodb-database-for-objectStorage') {
           sh 'docker compose -f docker-compose.yaml up -d'
         }
       }
    }

    stage('Build') {
      steps {
          sh 'mvn clean install'
      }
    }


    stage('SonarQube Analysis') {
       steps {
          withSonarQubeEnv('sonarqube') {
          sh "mvn clean verify sonar:sonar -Dsonar.projectKey=objectStorage_api -Dsonar.projectName='objectStorage_api' -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"
        }
      }
    }

    stage('upload to nexus') {
        steps{
            nexusArtifactUploader artifacts: [[artifactId: 'objectstorage-api', classifier: '', file: 'target/objectstorage-api-0.0.1-SNAPSHOT.jar', type: '.jar']], 
            credentialsId: 'nexus-cred', 
            groupId: 'accel-tech.net', 
            nexusUrl: '192.168.15.115:8081', 
            nexusVersion: 'nexus3', 
            protocol: 'http', 
            repository: 'objectStorage', 
            version: '0.0.1-SNAPSHOT'
        }
    }

    stage('build image Docker') {
       steps {
          script {
             def imageTag= "$DOCKER_USER/$IMAGE_NAME:v${env.BUILD_NUMBER}"
                  sh "docker build -t ${imageTag} ."   
          }
       }
    }
    stage('push image Docker') {
        steps {
            withCredentials([usernamePassword(credentialsId: 'docker_registry', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                script {
                    def imageTag= "$DOCKER_USER/$IMAGE_NAME:v${env.BUILD_NUMBER}"
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                     sh "docker push ${imageTag}"
                }
            }
        }
    }

    stage('Login to OpenShift') {
        steps {
            withCredentials([string(credentialsId: 'openshift-token', variable: 'TOKEN')]) {
                sh '''
                    oc login --token=$TOKEN --server=$OC_SERVER 
                    oc project $OPENSHIFT_PROJECT
                '''
            }
        }
    }
    stage('Deploy database to openshift') {
       steps {
          dir('mongodb-database-for-objectStorage') {
            git credentialsId: 'ssh-jenkins',
               url: 'https://github.com/bambandouraccel/mongodb-database-for-objectStorage.git',
               branch: 'main'
            // sh "oc delete configmap db-env"
            // sh "oc create configmap db-env --from-env-file=.env "
            sh " oc apply -f mongodb-deployment.yaml"
          }
       }
    }

    stage('Deploy objectStorage_api to openshift') {
        steps {
            sh "oc delete configmap os-env"
            sh "oc create configmap os-env --from-env-file=.env"
            sh 'oc project $OPENSHIFT_PROJECT'
            sh "sed -i 's|image: .*|image: ${DOCKER_USER}/${IMAGE_NAME}:v${env.BUILD_NUMBER}|' deployment.yaml"
            sh "oc apply -f deployment.yaml"
        }
    }

     
    
    
    


  }

 
}
