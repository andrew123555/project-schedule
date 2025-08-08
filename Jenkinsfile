pipeline {
    agent any

    environment {
        FRONTEND_REPO_URL = "https://github.com/andrew123555/project-shedule-React.git"
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
                    // 使用包含 git 的 Node.js 映像，而不是 alpine 版本
                    image 'node:20'
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
                            cat package.json | head -20
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
                            if [ -d "dist" ]; then
                                echo "Found dist directory:"
                                ls -la dist/
                            elif [ -d "build" ]; then
                                echo "Found build directory:"
                                ls -la build/
                            else
                                echo "Searching for any build output..."
                                find . -name "index.html" -type f
                                find . -name "*.js" -type f | grep -E "(assets|static)" | head -5
                            fi
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
                    
                    // 檢查應用程式是否正在運行（忽略 401 錯誤，因為這表示應用程式正常運行但需要認證）
                    def exitCode = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_EXTERNAL_PORT}/", returnStdout: true).trim()
                    
                    echo "HTTP Status Code: ${exitCode}"
                    
                    if (exitCode == "401" || exitCode == "200" || exitCode == "302") {
                        echo 'Application is up and running! (HTTP status: ' + exitCode + ')'
                    } else {
                        echo "Application health check failed with HTTP status: ${exitCode}"
                        // 顯示容器狀態以幫助調試
                        sh "docker-compose ps"
                        sh "docker-compose logs app"
                        error "Application is not responding correctly"
                    }
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