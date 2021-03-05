node {
    docker.image('maven:3.6.3-jdk-11').inside('-v /root/.m2:/root/.m2') {
        stage('Build') {
            sh 'mvn -B clean package'
            stash includes: '**/target/ecosystem-user-service.jar', name: 'jar'
        }
    }
}