package com.duck.config;

import com.duck.service.ghn.GhnServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Logger ghnLogger() {
        return LoggerFactory.getLogger(GhnServiceImpl.class);
    }
}
