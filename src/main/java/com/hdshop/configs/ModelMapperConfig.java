package com.hdshop.configs;

import com.hdshop.dtos.CategoryDTO;
import com.hdshop.entities.Category;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        /*
        modelMapper.typeMap(Category.class, CategoryDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getParent().getId(), CategoryDTO::setParentId));*/

        return modelMapper;
    }
}
