package com.example.demo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 這個註解告訴 Spring 這個類包含 Bean 定義
public class ModelMapperConfig {

    @Bean // 這個註解告訴 Spring 創建並管理一個 ModelMapper 的實例
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}