package com.hdshop.service.product.impl;

import com.hdshop.entity.product.Option;
import com.hdshop.entity.product.Product;
import com.hdshop.entity.product.ProductSku;
import com.hdshop.repository.product.ProductRepository;
import com.hdshop.repository.product.ProductSkuRepository;
import com.hdshop.service.product.ProductSkuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductSkuServiceImpl implements ProductSkuService {

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;

    public ProductSkuServiceImpl(ProductRepository productRepository,
                                 ProductSkuRepository productSkuRepository) {
        this.productRepository = productRepository;
        this.productSkuRepository = productSkuRepository;
    }

    @Override
    public List<ProductSku> addProductSkus(Product product, List<ProductSku> productSkus) {
        List<ProductSku> savedProductSku = productSkus.stream()
                .peek(option -> option.setProduct(product))
                .map(productSkuRepository::save)
                .collect(Collectors.toList());

        return savedProductSku;
    }
}
