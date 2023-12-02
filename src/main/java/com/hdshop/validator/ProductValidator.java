package com.hdshop.validator;

import com.hdshop.dto.product.ProductDTO;
import com.hdshop.entity.Product;
import com.hdshop.exception.InvalidException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductValidator {
    public void validate(Product product) {
        if (product.getName().isBlank()) {
            throw new InvalidException("product-name-must-not-be-empty");
        }
        if (product.getDescription().isBlank()) {
            throw new InvalidException("product-description-must-not-be-empty");
        }
        if (product.getCategory().getName().isBlank()) {
            throw new InvalidException("category-name-must-not-be-empty");
        }
        if (product.getQuantity() < 1) {
            throw new InvalidException("product-quantity-must-not-be-less-than-one");
        }
        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidException("product-price-must-not-be-less-than-zero");
        }
        if (product.getPromotionalPrice() != null && product.getPromotionalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidException("promotional-price-must-not-be-less-than-zero");
        }
    }

    public void validateUpdate(ProductDTO product) {
        if (product.getName().isBlank()) {
            throw new InvalidException("product-name-must-not-be-empty");
        }
        if (product.getDescription().isBlank()) {
            throw new InvalidException("product-description-must-not-be-empty");
        }
        if (product.getCategory().getName().isBlank()) {
            throw new InvalidException("category-name-must-not-be-empty");
        }
        if (product.getQuantity() < 1) {
            throw new InvalidException("product-quantity-must-not-be-less-than-one");
        }
        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidException("product-price-must-not-be-less-than-zero");
        }
        if (product.getPromotionalPrice() != null && product.getPromotionalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidException("promotional-price-must-not-be-less-than-zero");
        }
    }

    public Product normalizeInput(Product product) {
        product.setName(normalizeInputString(product.getName()));
        product.setDescription(normalizeInputString(product.getDescription()));
        return product;
    }

    public String normalizeInputString(String input) {
        return input.replaceAll("\\s{2,}", " ").trim();
    }
}
