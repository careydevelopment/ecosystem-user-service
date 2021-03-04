pipeline {
    agent any

    tools {
        maven "M3"
    }

    environment { 
        POM_VERSION= sh (returnStdout: true, script: 'mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout').trim()
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
      
        stage('Docker Build') {
            steps {
                //sh 'POM_VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout`'
                sh 'echo $POM_VERSION'
                sh 'cp /etc/careydevelopment/ecosystem.properties .'
                sh 'cp /etc/careydevelopment/server.p12 .'
                
                sh 'sudo docker build --tag ecosystem-user-service:$POM_VERSION .'
            } 
        }
        
        stage ('Docker Run') {
            steps {
                sh 'sudo docker rm --force bixis-service || true'
                sh 'sudo docker run -d -t -v /etc/careydevelopment:/etc/careydevelopment -p 32010:32010 --name ecosystem-user-service ecosystem-user-service:$POM_VERSION'
            }
        }
    }
}
