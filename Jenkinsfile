node {
	def app
	
	stage('Clone repository') {               
    	git branch: '0.2.6-devops-work',
        credentialsId: 'GitHub Credentials',
        url: 'https://github.com/careydevelopment/ecosystem-user-service.git'
	
        sh "ls -lat"  
    } 

	stage('Build JAR') {
    	docker.image('maven:3.6.3-jdk-11').inside('-v /root/.m2:/root/.m2') {
        	sh 'mvn -B clean package'
        	stash includes: '**/target/ecosystem-user-service.jar', name: 'jar'
    	}
    }
     
    stage('Build Image') {
    	unstash 'jar'
    	sh 'ls -la'
    	sh 'ls -la ./target'
		app = docker.build("0.2.6")
    }
    
    //stage('Push') {
    //	docker.withRegistry('https://registry.hub.docker.com', 'docker-hub') {            
	//		app.push("0.2.6")            
    //    }    
    //}
}