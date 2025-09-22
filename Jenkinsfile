pipeline {
    // We use a Docker agent to provide a consistent and isolated environment.
    // The maven:3.8.6-openjdk-17 image contains both Maven and the required Java 17 JDK.
    agent {
        docker {
            image 'maven:3.8.6-openjdk-17'
            // Use alwaysPull to ensure the latest image is used
            args '-v $HOME/.m2:/root/.m2' // Cache Maven dependencies
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
