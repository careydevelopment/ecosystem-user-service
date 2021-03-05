node {
	def app

	stage('Build JAR') {
    	docker.image('maven:3.6.3-jdk-11').inside('-v /root/.m2:/root/.m2') {
        	sh 'mvn -B clean package'
        	stash includes: '**/target/ecosystem-user-service.jar', name: 'jar'
    	}
    }
    
    stage('Build Image') {
    	unstash 'jar'
		app = docker.build("0.2.6")
    }
    
    //stage('Push') {
    //	docker.withRegistry('https://registry.hub.docker.com', 'docker-hub') {            
	//		app.push("0.2.6")            
    //    }    
    //}
}