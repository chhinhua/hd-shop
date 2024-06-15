package com.duck.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(dateToStringConverter());
        return modelMapper;
    }

    private AbstractConverter<ZonedDateTime, String> dateToStringConverter() {
        return new AbstractConverter<ZonedDateTime, String>() {
            @Override
            protected String convert(ZonedDateTime source) {
                return DateTimeConfig.formatDateTime(source);
            }
        };
    }
}
