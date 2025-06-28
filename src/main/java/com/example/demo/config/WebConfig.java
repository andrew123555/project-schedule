package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
@EnableWebMvc // 確保啟用 Spring MVC 的完整配置控制
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 這是關鍵：將靜態資源路徑限制在明確的子目錄中，而不是 /**
        // 假設您的前端打包後的靜態文件都在 /static/ 或 /public/ 目錄下
        // 如果您的前端資源直接放在根目錄，則需要調整
        registry.addResourceHandler("/static/**") // 匹配 /static/ 下的所有資源
                .addResourceLocations("classpath:/static/", "classpath:/public/") // 查找位置
                .setCachePeriod(3600) // 設置緩存時間
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        registry.addResourceHandler("/assets/**") // 如果您有 /assets/ 這樣的路徑
                .addResourceLocations("classpath:/assets/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        // 如果您的前端應用程式是一個單頁應用 (SPA)，並且您希望所有未匹配的路由都返回 index.html
        // 您可能需要一個 Fallback Controller 或更複雜的配置。
        // 但目前的問題是 /api/activities 被靜態資源處理器攔截。

        // 確保您的 API 路徑（例如 /api/**）不會被這裡的靜態資源處理器攔截。
        // 通過明確指定靜態資源的前綴，可以避免與 /api/activities 的衝突。
    }

    // 您可能還需要配置一個默認視圖控制器，用於單頁應用程式的根路徑
    // @Override
    // public void addViewControllers(ViewControllerRegistry registry) {
    //     registry.addViewController("/").setViewName("forward:/index.html");
    //     registry.addViewController("/{spring:\\w+}").setViewName("forward:/index.html");
    //     registry.addViewController("/**/{spring:\\w+}").setViewName("forward:/index.html");
    //     registry.addViewController("/error").setViewName("forward:/index.html");
    // }
}