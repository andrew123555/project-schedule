pipeline {
    agent any

    environment {
        FRONTEND_REPO_URL = "https://github.com/andrew123555/project-shedule-React.git"
        // FRONTEND_CREDENTIALS_ID = "your-github-credentials-id" // 如果是私有儲存庫請取消註釋
        APP_EXTERNAL_PORT = "8081"
    }

    stages {
        stage('Checkout Backend Source Code') {
            steps {
                echo 'Checking out backend source code from Git...'
            }
        }

        stage('Build Frontend') {
            agent {
                docker {
                    image 'node:20-alpine' // 更新到 Node.js 20 以支援 React Router 7
                    args '-u root'
                }
            }
            steps {
                script {
                    echo "Cloning frontend repository: ${FRONTEND_REPO_URL}..."
                    git branch: 'master', url: env.FRONTEND_REPO_URL

                    echo 'Checking workspace contents after git clone...'
                    sh 'pwd && ls -la'

                    dir('project-shedule-React') {
                        echo 'Current directory and contents:'
                        sh 'pwd && ls -la'

                        echo 'Installing frontend dependencies...'
                        sh 'npm install'

                        echo 'Building frontend application...'
                        sh 'npm run build'

                        echo 'Checking build output...'
                        sh 'pwd && ls -la'
                        
                        // 檢查多個可能的建置目錄
                        sh '''
                            if [ -d "dist" ]; then
                                echo "Found dist directory:"
                                ls -la dist/
                                BUILD_DIR="dist"
                            elif [ -d "build" ]; then
                                echo "Found build directory:"
                                ls -la build/
                                BUILD_DIR="build"
                            else
                                echo "Searching for build output..."
                                find . -name "index.html" -type f
                                find . -name "*.js" -type f -path "*/assets/*" | head -5
                            fi
                        '''

                        // 根據實際的建置目錄來 stash
                        script {
                            if (sh(script: 'test -d dist', returnStatus: true) == 0) {
                                echo 'Stashing dist directory...'
                                stash includes: 'dist/**', name: 'frontend-build-artifacts'
                            } else if (sh(script: 'test -d build', returnStatus: true) == 0) {
                                echo 'Stashing build directory...'
                                stash includes: 'build/**', name: 'frontend-build-artifacts'
                            } else {
                                error 'Could not find build output directory (dist or build)'
                            }
                        }
                    }
                }
            }
        }

        stage('Build and Deploy with Docker Compose') {
            steps {
                script {
                    echo 'Unstashing frontend build artifacts...'
                    unstash 'frontend-build-artifacts'

                    echo 'Checking unstashed files...'
                    sh 'ls -la'

                    // 複製前端建置檔案到後端靜態資源目錄
                    sh '''
                        if [ -d "dist" ]; then
                            echo "Copying from dist directory..."
                            mkdir -p src/main/resources/static/
                            cp -R dist/* src/main/resources/static/
                        elif [ -d "build" ]; then
                            echo "Copying from build directory..."
                            mkdir -p src/main/resources/static/
                            cp -R build/* src/main/resources/static/
                        else
                            echo "Error: No build directory found!"
                            exit 1
                        fi
                    '''

                    echo 'Verifying static files copied:'
                    sh 'ls -la src/main/resources/static/'

                    echo 'Building and deploying services with Docker Compose...'
                    sh "docker-compose up --build -d"
                    echo "Services deployed with Docker Compose. App accessible on port ${APP_EXTERNAL_PORT}."
                }
            }
        }

        stage('Verify Application') {
            steps {
                script {
                    echo 'Waiting for application to be ready...'
                    sh "sleep 60"
                    echo "Checking application health at http://localhost:${APP_EXTERNAL_PORT}/"
                    sh "curl -f http://localhost:${APP_EXTERNAL_PORT}/ || exit 1"
                    echo 'Application is up and running!'
                }
            }
        }
    }

    post {
        always {
            echo 'Stopping Docker Compose services...'
            sh "docker-compose down || true"
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