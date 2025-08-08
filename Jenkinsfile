pipeline {
    agent any

    environment {
        APP_EXTERNAL_PORT = "8081" // 宿主機外部訪問埠號
        // 請替換為你的 React 前端 GitHub 儲存庫的 HTTPS URL
        FRONTEND_REPO_URL = "https://github.com/andrew123555/project-shedule-React.git"
        // 請替換為你的前端儲存庫的憑證 ID (如果它是私有的)
        // 如果是公開儲存庫，可以將此行註釋掉或留空
        // FRONTEND_CREDENTIALS_ID = "your-github-credentials-id" 
    }

    stages {
        stage('Checkout Backend Source Code') {
            steps {
                echo 'Checking out backend source code from Git...'
                // 這裡會自動拉取當前 Jenkins Pipeline 配置的後端專案
            }
        }

        stage('Build Frontend') {
            steps {
                script {
                    echo "Cloning frontend repository: ${FRONTEND_REPO_URL}..."
                    // 克隆前端儲存庫到 Jenkins 工作區的一個子目錄 (例如 'frontend-app')
                    // 如果前端儲存庫是私有的，請使用 credentialsId
                    git branch: 'master', credentialsId: env.FRONTEND_CREDENTIALS_ID, url: env.FRONTEND_REPO_URL

                    // 進入前端專案目錄
                    dir('project-shedule-React') { // 這裡的名稱應該是你的前端儲存庫克隆下來的資料夾名稱
                        echo 'Installing frontend dependencies...'
                        sh 'npm install'

                        echo 'Building frontend application...'
                        sh 'npm run build' # 這會產生靜態檔案，通常在 'build' 目錄
                    }
                }
            }
        }

        stage('Build and Deploy with Docker Compose') {
            steps {
                script {
                    echo 'Building and deploying services with Docker Compose...'
                    // 將前端建置後的靜態檔案移動到後端專案的靜態資源目錄
                    // 假設前端建置後的檔案在 '你的前端專案名/build'
                    // 假設後端靜態資源目錄是 'src/main/resources/static'
                    sh "cp -R 你的前端專案名/build/. src/main/resources/static/"

                    // 使用 docker-compose up --build -d 來建置映像檔並啟動服務
                    // --build 強制重新建置映像檔 (確保後端重新建置並包含新的前端檔案)
                    // -d 在後台運行服務
                    sh "docker-compose up --build -d"
                    echo "Services deployed with Docker Compose. App accessible on port ${APP_EXTERNAL_PORT}."
                }
            }
        }

        stage('Verify Application') {
            steps {
                script {
                    echo 'Waiting for application to be ready...'
                    sh "sleep 60" # 增加等待時間，確保資料庫和Spring Boot都完全啟動
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
            sh "docker-compose down || true" # 確保停止並移除服務
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
