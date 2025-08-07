# 階段 1：建置階段
# 使用一個包含 OpenJDK 和 Maven 的基礎映像檔來建置專案
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

# 執行 Maven 建置，產生可執行的 JAR 檔案，並跳過測試
# 你的日誌顯示最終產物是 demo-0.0.1-SNAPSHOT.jar
RUN mvn package -DskipTests

# 階段 2：運行階段
# 使用一個輕量級的 Java 運行時環境作為基礎映像檔
FROM eclipse-temurin:17-jre-alpine

# 將建置階段產生的 JAR 檔案複製到運行階段的映像檔中
# 注意：這裡的檔案名稱必須與 Maven 實際打包出來的名稱完全一致
# 根據你的日誌，它是 demo-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# 暴露應用程式運行的埠號
# Spring Boot 應用程式預設運行在 8080 埠
EXPOSE 8080

# 啟動應用程式
# 使用 java -jar 命令運行打包好的 JAR 檔案
CMD ["java", "-jar", "/app/app.jar"]
