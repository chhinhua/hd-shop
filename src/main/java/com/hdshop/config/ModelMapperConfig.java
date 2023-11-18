package com.hdshop.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addConverter(dateToStringConverter());

        return modelMapper;
    }

    private AbstractConverter<Date, String> dateToStringConverter() {
        return new AbstractConverter<Date, String>() {
            @Override
            protected String convert(Date source) {
                return DateTimeConfig.formatDateTime(source);
            }
        };
    }
}
