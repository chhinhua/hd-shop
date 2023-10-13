package com.hdshop.config;

import com.hdshop.dto.CategoryDTO;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.entity.Category;
import com.hdshop.entity.Product;
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

        modelMapper.typeMap(Category.class, CategoryDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getParent().getId(), CategoryDTO::setParentId));

        modelMapper.typeMap(Product.class, ProductDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getCategory().getId(), ProductDTO::setCategoryId));

        return modelMapper;
    }
}
