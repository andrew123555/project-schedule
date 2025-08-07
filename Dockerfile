# 階段 1：建置階段
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder

# 設定工作目錄
WORKDIR /app

# 複製 Maven 專案設定檔 pom.xml
COPY pom.xml .

# 下載所有 Maven 依賴項
# -B 表示非互動模式
RUN mvn dependency:go-offline -B

# 複製所有專案原始碼
COPY src ./src

# 執行 Maven 建置，產生 .war 檔案
RUN mvn package

# 階段 2：運行階段
# 使用 Tomcat 官方映像檔
FROM tomcat:9.0-jre17-temurin-focal

# 移除 Tomcat 預設的 ROOT 應用程式
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# 從建置階段複製產生的 .war 檔案到 Tomcat 的 webapps 目錄
# 假設你的 .war 檔案名稱是 target/your-app.war
COPY --from=builder /app/target/demo.war /usr/local/tomcat/webapps/ROOT.war

# 暴露 Tomcat 運行的埠號
EXPOSE 8080

# 啟動 Tomcat 伺服器
CMD ["catalina.sh", "run"]