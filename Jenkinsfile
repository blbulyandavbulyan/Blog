pipeline {
    agent {
        docker {
            image 'maven:3.8.5-openjdk-17'
            args '-v $HOME/.m2:/root/.m2'
        }
    }

    stages {

        stage('Compile') {
            steps {
                echo 'Building the project with Maven...'
                sh 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running unit and integration tests...'
                sh 'mvn verify'
            }
        }
    }
}
