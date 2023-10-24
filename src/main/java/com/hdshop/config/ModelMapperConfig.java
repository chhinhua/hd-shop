package com.hdshop.config;

import com.hdshop.component.CreateProductDTOToProductConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        /*modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.typeMap(Category.class, CategoryDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getParent().getId(), CategoryDTO::setParentId));

        modelMapper.typeMap(Product.class, ProductDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getCategory().getId(), ProductDTO::setCategoryId));*/

        // Định nghĩa Converter và tiêm ModelMapper vào để sử dụng trong Converter
        CreateProductDTOToProductConverter converter = new CreateProductDTOToProductConverter(modelMapper);
        modelMapper.addConverter(converter);
        return modelMapper;
    }
}
