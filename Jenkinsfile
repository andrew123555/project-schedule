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
                    image 'node:20-alpine'
                    args '-u root'
                }
            }
            steps {
                script {
                    echo "Cloning frontend repository: ${FRONTEND_REPO_URL}..."
                    
                    // 清理工作區並重新 clone 前端專案
                    sh 'rm -rf frontend-temp && mkdir frontend-temp'
                    
                    dir('frontend-temp') {
                        // 直接 clone 前端專案到當前目錄
                        sh "git clone ${FRONTEND_REPO_URL} ."
                        
                        echo 'Checking cloned frontend project structure:'
                        sh 'pwd && ls -la'
                        
                        // 確認必要檔案存在
                        sh '''
                            if [ ! -f "package.json" ]; then
                                echo "Error: package.json not found!"
                                exit 1
                            fi
                            echo "Found package.json, proceeding with build..."
                        '''

                        echo 'Installing frontend dependencies...'
                        sh 'npm install'

                        echo 'Building frontend application...'
                        sh 'npm run build'

                        echo 'Checking build output after npm run build...'
                        sh 'pwd && ls -la'
                        
                        // 確認建置目錄存在並顯示內容
                        sh '''
                            echo "=== Searching for build output ==="
                            find . -type d -name "dist" -o -name "build" | while read dir; do
                                echo "Found build directory: $dir"
                                ls -la "$dir"
                            done
                            
                            echo "=== Searching for index.html files ==="
                            find . -name "index.html" -type f | while read file; do
                                echo "Found index.html: $file"
                                dirname "$file"
                            done
                        '''

                        // 根據實際情況 stash 建置檔案
                        script {
                            def buildDirExists = sh(script: 'test -d dist', returnStatus: true) == 0
                            def buildDirExists2 = sh(script: 'test -d build', returnStatus: true) == 0
                            
                            if (buildDirExists) {
                                echo 'Found dist directory, stashing...'
                                sh 'ls -la dist/'
                                stash includes: 'dist/**', name: 'frontend-build-artifacts'
                            } else if (buildDirExists2) {
                                echo 'Found build directory, stashing...'
                                sh 'ls -la build/'
                                stash includes: 'build/**', name: 'frontend-build-artifacts'
                            } else {
                                // 最後的檢查：在整個專案中搜尋建置檔案
                                echo 'Attempting to locate build files in entire project...'
                                sh '''
                                    # 尋找可能的建置檔案
                                    find . -name "index.html" -exec dirname {} \\; | sort -u | while read dir; do
                                        if [ "$dir" != "." ] && [ "$dir" != "./public" ]; then
                                            echo "Potential build directory found: $dir"
                                            ls -la "$dir"
                                        fi
                                    done
                                '''
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