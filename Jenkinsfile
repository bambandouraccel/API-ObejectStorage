pipeline {
  agent any
  environment {
        APP_NAME= "objectStorage"
        OPENSHIFT_PROJECT = 'devops'
        IMAGE_NAME= "objectStorage_api"
        DOCKER_USER= "mina0423"
        OC_SERVER= "https://api.ocp.heritage.africa:6443"
    }



  stages {
    
    // stage('Start Database') {
    //   steps {
    //     sh 'docker-compose -f docker-compose.yml up -d'
    //   }
    // }

    stage('Build') {
      steps {
          sh 'mvn clean install -DskipTests'
      }
    }

    stage('SonarQube Analysis') {
       steps {
          withSonarQubeEnv('sonarqube') {
          sh "mvn clean verify sonar:sonar -Dsonar.projectKey=objectStorage_api -Dsonar.projectName='objectStorage_api'"
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

    stage('Deploy to openshift') {
        steps {
            sh 'oc project $OPENSHIFT_PROJECT'
            sh "sed -i 's|image: .*|image: ${DOCKER_USER}/${IMAGE_NAME}:v${env.BUILD_NUMBER}|' deployment.yaml"
            sh "oc apply -f deployment.yaml"
        }
    }
    
    


  }

 
}
