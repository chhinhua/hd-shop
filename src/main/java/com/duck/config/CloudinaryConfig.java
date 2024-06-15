package com.duck.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${app.cloudinary.cloud_name}")
    private String cloud_name;
    @Value("${app.cloudinary.api_key}")
    private String api_key;
    @Value("${app.cloudinary.api_secret}")
    private String api_secret;

    @Bean
    public Cloudinary getCloudinary(){
        Map config = new HashMap();
        config.put("cloud_name", cloud_name);
        config.put("api_key", api_key);
        config.put("api_secret", api_secret);
        config.put("secure", true);
        return new Cloudinary(config);
    }
}
