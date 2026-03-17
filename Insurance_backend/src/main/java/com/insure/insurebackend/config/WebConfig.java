package com.insure.insurebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Profile images
        registry.addResourceHandler("/profile-images/**")
                .addResourceLocations("file:uploads/profile/");

        // Documents
        registry.addResourceHandler("/documents/**")
                .addResourceLocations("file:uploads/documents/");
    }
}