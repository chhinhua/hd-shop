package com.hdshop.service.product.impl;

import com.hdshop.entity.product.Product;
import com.hdshop.entity.product.SkuValue;
import com.hdshop.entity.product.SkuValueId;
import com.hdshop.repository.product.SkuValueRepository;
import com.hdshop.service.product.SkuValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkuValueServiceImpl implements SkuValueService {
    private final SkuValueRepository skuValueRepository;

    @Override
    public List<SkuValue> addSkuValues(Product product, List<SkuValue> skuValues) {
        /*List<SkuValue> savedSkuValues = skuValues.stream()
                .peek(skuValue -> skuValue.setSkuValueId(
                        new SkuValueId(product.getId(), product.gets)
                ))
                .map(skuValueRepository::save)
                .collect(Collectors.toList());
        return savedOptions;*/
        return null;
    }
}
