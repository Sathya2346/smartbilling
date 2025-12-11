package com.example.smartbilling.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String barcodePath = System.getProperty("user.dir") + "/uploads/barcodes/";

        registry.addResourceHandler("/barcodes/**")
                .addResourceLocations("file:///" + barcodePath);
    }
}