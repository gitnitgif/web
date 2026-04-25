package com.fjdialect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // user.dir = backend directory
        // ../uploads = project root's uploads folder
        // Spring strips /uploads/ prefix from URL, so /uploads/audio/fuzhou.mp3 -> audio/fuzhou.mp3
        String uploadDir = new File(System.getProperty("user.dir"), "../uploads").getAbsolutePath();
        String location = "file:" + uploadDir.replace("\\", "/") + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
