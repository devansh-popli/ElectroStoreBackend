package com.lcwd.store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure this points to your project's absolute path
        String uploadDir = "file:images/product/";  // No leading '/'

        // This will map the /images/product/* URL pattern to the local files in the uploads folder
        registry.addResourceHandler("images/product/**")
                .addResourceLocations(uploadDir);
    }
}

