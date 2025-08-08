pipeline {
    agent any // 整個 Pipeline 的默認代理，如果沒有特定階段的代理，則在此上運行

    environment {
        FRONTEND_REPO_URL = "https://github.com/andrew123555/project-shedule-React.git" // 請確認這是你前端專案的正確 URL
        // 請替換為你的前端儲存庫的憑證 ID (如果它是私有的)
        // 如果是公開儲存庫，可以將此行註釋掉或留空
        // FRONTEND_CREDENTIALS_ID = "your-github-credentials-id"
        APP_EXTERNAL_PORT = "8081" // 宿主機外部訪問埠號
    }

    stages {
        stage('Checkout Backend Source Code') {
            steps {
                echo 'Checking out backend source code from Git...'
                // 這裡會自動拉取當前 Jenkins Pipeline 配置的後端專案
            }
        }

        stage('Build Frontend') {
            // 這個階段將在一個新的 Docker 容器中運行，該容器基於 Node.js 映像檔
            agent {
                docker {
                    image 'node:18-alpine' // 使用一個包含 Node.js 的輕量級映像檔
                    args '-u root' // 在容器內以 root 用戶運行，以避免權限問題
                }
            }
            steps {
                script {
                    echo "Cloning frontend repository: ${FRONTEND_REPO_URL}..."
                    // git 命令將在 'node:18-alpine' 容器內執行
                    // 克隆前端儲存庫到 Jenkins 工作區的一個子目錄 (例如 'project-shedule-React')
                    git branch: 'master', credentialsId: env.FRONTEND_CREDENTIALS_ID, url: env.FRONTEND_REPO_URL

                    echo 'Installing frontend dependencies...'
                    // 在 'project-shedule-React' 資料夾內執行 npm install
                    sh 'npm install --prefix project-shedule-React'

                    echo 'Building frontend application...'
                    // 在 'project-shedule-React' 資料夾內執行 npm run build
                    sh 'npm run build --prefix project-shedule-React' // 這會產生靜態檔案，通常在 'project-shedule-React/dist' 目錄

                    // 現在，在 Jenkins 工作區的根目錄執行 ls 和 stash
                    echo 'Listing frontend build directory contents before stashing...'
                    sh 'ls -R project-shedule-React/dist/' // 檢查 'project-shedule-React/dist/' 相對於工作區根目錄

                    // 將前端建置後的檔案打包並存儲起來，以便後續階段使用
                    // stash 的 includes 路徑是相對於 Jenkins 工作區的根目錄
                    stash includes: 'project-shedule-React/dist/**', name: 'frontend-build-artifacts'
                }
            }
        }

        stage('Build and Deploy with Docker Compose') {
            // 這個階段將回到默認的 Jenkins 代理上運行
            steps {
                script {
                    echo 'Unstashing frontend build artifacts...'
                    // 在此階段開始時，將之前存儲的前端建置檔案解包到當前工作區
                    // unstash 會將 'dist/**' 直接解包到當前工作區的根目錄
                    unstash 'frontend-build-artifacts'

                    echo 'Building and deploying services with Docker Compose...'
                    // 將前端建置後的靜態檔案移動到後端專案的靜態資源目錄
                    // 由於 unstash 將 'dist' 資料夾直接放在了工作區根目錄，所以 cp 路徑需要調整
                    sh "cp -R dist/. src/main/resources/static/"

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
                    sh "sleep 60" // 增加等待時間，確保資料庫和Spring Boot都完全啟動
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
            sh "docker-compose down || true" // 確保停止並移除服務
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
