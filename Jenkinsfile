node {
	def app
	def image = 'brianmcarey/ecosystem-user-service'
	
	stage('Clone repository') {               
    	git branch: '0.2.6-devops-work',
        	credentialsId: 'GitHub Credentials',
        	url: 'https://github.com/careydevelopment/ecosystem-user-service.git'
    } 

	stage('Build JAR') {
    	docker.image('maven:3.6.3-jdk-11').inside('-v /root/.m2:/root/.m2') {
        	sh 'mvn -B clean package'
        	stash includes: '**/target/ecosystem-user-service.jar', name: 'jar'
    	}
    }
     
    stage('Build Image') {
    	unstash 'jar'
		app = docker.build image + ':$BUILD_NUMBER'
    }
    
    stage('Push') {
    	docker.withRegistry('https://registry.hub.docker.com', 'docker-hub') {            
			app.push()
        }    
    }
    
    stage('Cleanup') {
		sh 'docker rmi ' + image + ':$BUILD_NUMBER'
		sh 'docker rmi registry.hub.docker.com/' + image + ':$BUILD_NUMBER'
    }
}