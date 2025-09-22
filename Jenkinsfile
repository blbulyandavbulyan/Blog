pipeline {
    agent {
        docker {
            image 'maven:3.8.5-openjdk-17'
            args '-v $HOME/.m2:/root/.m2'
        }
    }

    stages {
        stage('Clone') {
            steps {
                echo 'Cloning the Git repository...'
                git url: env.GIT_REPO_URL, branch: env.GIT_BRANCH
            }
        }

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
