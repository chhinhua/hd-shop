package com.hdshop.component;

import com.hdshop.dto.product.CreateProductDTO;
import com.hdshop.entity.product.Option;
import com.hdshop.entity.product.Product;
import com.hdshop.entity.product.ProductSku;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CreateProductDTOToProductConverter implements Converter<CreateProductDTO, Product> {
    private final ModelMapper modelMapper;
    @Override
    public Product convert(MappingContext<CreateProductDTO, Product> context) {
        CreateProductDTO source = context.getSource();
        Product destination = new Product();

        // Ánh xạ các thuộc tính cơ bản
        destination.setName(source.getName());
        destination.setDescription(source.getDescription());
        // ...

        // Ánh xạ danh sách options
        List<Option> options = source.getOptions().stream()
                .map(optionDTO -> modelMapper.map(optionDTO, Option.class))
                .collect(Collectors.toList());
        destination.setOptions(options);

        // Ánh xạ danh sách skus
        List<ProductSku> skus = source.getSkus().stream()
                .map(skuDTO -> modelMapper.map(skuDTO, ProductSku.class))
                .collect(Collectors.toList());
        destination.setSkus(skus);

        return destination;
    }
}

