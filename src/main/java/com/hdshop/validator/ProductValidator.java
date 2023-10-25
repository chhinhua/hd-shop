package com.hdshop.validator;

import com.hdshop.entity.product.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {
    public Product normalizeInput(Product product) {
        product.setName(normalizeInputString(product.getName()));
        product.setDescription(normalizeInputString(product.getDescription()));
        return product;
    }

    public String normalizeInputString(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }
}
