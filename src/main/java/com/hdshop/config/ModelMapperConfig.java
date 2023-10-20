package com.hdshop.config;

import com.hdshop.dto.CategoryDTO;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.entity.Category;
import com.hdshop.entity.product.Product;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

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

        /*Converter<List<?>, ArrayList<?>> listConverter = context -> {
            List<?> source = context.getSource();
            if (source == null) return null;
            return new ArrayList<>(source);
        };

        modelMapper.createTypeMap(Product.class, ProductDTO.class)
                .addMappings(mapper -> {
                    mapper.using(listConverter).map(Product::getOptions, ProductDTO::setOptions);
                    mapper.using(listConverter).map(Product::getSkus, ProductDTO::setSkus);
                });
*/
        return modelMapper;
    }
}
