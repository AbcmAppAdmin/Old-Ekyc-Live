package com.abcm.esign_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${clientUrl}")
    private String clientUrl;  

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        
        registry.addMapping("/api/esign/**")
                .allowedOrigins(clientUrl) 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  
                .allowedHeaders("*");  
    }
}