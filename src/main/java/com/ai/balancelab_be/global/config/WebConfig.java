package com.ai.balancelab_be.global.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${app.profiles-dir}")
    private String profilesDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String normalizedPath = Paths.get(uploadDir).normalize().toString().replace("\\", "/");
        String resourceLocation = "file:" + normalizedPath + "/";

        File dir = new File(normalizedPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            logger.info("Upload directory {}: {}", normalizedPath, created ? "created" : "failed to create");
        }

        // uploads와 하위 폴더(profiles 등) 매핑
        logger.info("Mapping /uploads/** to {}", resourceLocation);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
