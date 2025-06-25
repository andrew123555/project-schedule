package com.example.demo.config;

//src/main/java/com/yourprojectname/config/WebConfig.java (建立此檔案)


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

 @Override
 public void addCorsMappings(CorsRegistry registry) {
     registry.addMapping("/**") // 將 CORS 應用於所有端點
             .allowedOrigins("http://localhost:3000") // 允許您的 React 應用程式的來源
             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的 HTTP 方法
             .allowedHeaders("*") // 允許所有標頭
             .allowCredentials(true); // 允許傳送 cookie/認證標頭
 }
}