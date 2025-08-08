# 階段 1：建置 Spring Boot 後端
# 使用 Maven 和 Java 17 的 Alpine 版本作為後端建置環境
FROM maven:3.9.5-eclipse-temurin-17-alpine AS backend-builder

# 設定後端專案的工作目錄
WORKDIR /app

# 複製 Maven 專案設定檔 pom.xml
COPY pom.xml .

# 下載所有 Maven 依賴項
# -B 表示非互動模式
RUN mvn dependency:go-offline -B

# 複製所有後端原始碼
COPY src ./src

# 注意：這裡不再有前端建置階段，前端靜態檔案將從 Jenkins 工作區複製過來
# 因此，這裡不再需要 COPY --from=frontend-builder

# 執行 Maven 建置，產生可執行的 JAR 檔案，並跳過測試
# 根據你之前的日誌，最終產物是 demo-0.0.1-SNAPSHOT.jar
RUN mvn package -DskipTests

# 階段 2：運行 Spring Boot 應用程式
# 使用輕量級的 Java 17 JRE 運行環境
FROM eclipse-temurin:17-jre-alpine

# 設定應用程式運行目錄
WORKDIR /app

# 複製建置階段產生的 JAR 檔案到運行階段的映像檔中
# 注意：這裡的檔案名稱必須與 Maven 實際打包出來的名稱完全一致
COPY --from=backend-builder /app/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# 暴露應用程式運行的埠號 (Spring Boot 預設 8080)
EXPOSE 8080

# 啟動應用程式
CMD ["java", "-jar", "/app/app.jar"]
