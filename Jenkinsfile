pipeline {
	agent none
	
    options {
        skipStagesAfterUnstable()
    }
    stages {
        stage('Build') {
        	agent {
		        docker {
		            image 'maven:3.6.3-jdk-11'
		            args '-v /root/.m2:/root/.m2'
		        }
		    }
        	
            steps {
                sh 'mvn -B -DskipTests clean package'
                sh 'ls -la'
                sh 'ls ./target'
                stash includes: '**/target/ecosystem-user-service.jar', name: 'jar'
            }
        }
        //stage('Test') { 
        //    steps {
        //        sh 'mvn test' 
        //    }
        //    post {
        //        always {
        //            junit 'target/surefire-reports/*.xml' 
        //        }
        //    }
        // }
    }
}