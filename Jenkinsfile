pipeline {
    agent any

    environment {
        DOCKER_IMAGE_NAME = "my-nodejs-app"
        CONTAINER_NAME = "my-nodejs-app-container"
        HOST_PORT = "8081"
        CONTAINER_PORT = "3000"
    }

    stages {
        stage('Checkout Source Code') {
            steps {
                echo 'Checking out source code...'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} ."
                    echo "Docker image ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} built."
                }
            }
        }

        stage('Deploy to Docker') {
            steps {
                script {
                    echo 'Stopping and removing old container...'
                    sh "docker stop ${CONTAINER_NAME} || true"
                    sh "docker rm ${CONTAINER_NAME} || true"

                    echo "Running new container..."
                    sh "docker run -d -p ${HOST_PORT}:${CONTAINER_PORT} --name ${CONTAINER_NAME} ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}"
                    echo "Container ${CONTAINER_NAME} deployed on port ${HOST_PORT}."
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
