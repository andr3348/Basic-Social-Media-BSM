package com.basicsocialmedia.backend.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // apply CORS to all /api endpoints
                .allowedOrigins("https://localhost:5432")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","PATCH")
                .allowedHeaders("*")
                .allowCredentials(true); // allow sending cookies/auth headers
    }
}
