package com.duck.validator;

import com.duck.dto.product.ProductDTO;
import com.duck.entity.Option;
import com.duck.entity.Product;
import com.duck.entity.ProductSku;
import com.duck.exception.InvalidException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Provides validation methods for {@link Product} and {@link ProductDTO} objects.
 *
 * <p>Handles the validation rules for creating and updating products to ensure all fields meet the required conditions.</p>
 *
 * <p>Normalization of input strings is also provided to clean up the data before processing.</p>
 *
 * <p>Author: Chhin Hua 👨‍💻</p>
 * <p>Date: 26/10/2023 🗓</p>
 */
@Component
public class ProductValidator {
    @Autowired
    private ModelMapper mapper;

    /**
     * Validates the given {@link Product} object for creation.
     *
     * @param product the {@link Product} object to be validated
     * @throws InvalidException if any validation rule is violated
     */
    public void validateCreate(Product product) {
        ProductDTO dto = mapper.map(product, ProductDTO.class);
        validateCommonFields(dto);
        validateOptions(product.getOptions());
        validateSkus(product.getSkus());
    }

    /**
     * Validates the given {@link ProductDTO} object for update.
     *
     * @param product the {@link ProductDTO} object to be validated
     * @throws InvalidException if any validation rule is violated
     */
    public void validateUpdate(ProductDTO product) {
        validateCommonFields(product);
    }

    /**
     * Validates common fields the given {@link ProductDTO} object.
     *
     * <ul>
     *     <li>1. Validate product name</li>
     *     <li>2. Validate product description</li>
     *     <li>3. Validate category name</li>
     *     <li>4. Validate discount percentage</li>
     *     <li>5. Validate original price</li>
     * </ul>
     *
     * @param product the {@link ProductDTO} object to be validated
     * @throws InvalidException if any validation rule is violated
     */
    private void validateCommonFields(ProductDTO product) {
        if (product.getName().isBlank()) {
            throw new InvalidException("product.name-must-not-be-empty");
        }
        if (product.getDescription().isBlank()) {
            throw new InvalidException("product.description-must-not-be-empty");
        }
        if (product.getCategory().getName().isBlank()) {
            throw new InvalidException("category.name-must-not-be-empty");
        }
        if (product.getPercentDiscount() < 0 || product.getPercentDiscount() > 100) {
            throw new InvalidException("discount_percentage-must-have-a-value-between-0-and-100");
        }
        if (product.getOriginalPrice() == null || product.getOriginalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidException("product.original_price-must-not-be-less-than-zero");
        }
    }

    private void validateOptions(List<Option> options) {
        options.forEach(option -> {
            if (option.getOptionName().isBlank()) {
                throw new InvalidException("option_name-must-not-be-empty");
            }
        });
    }

    private void validateSkus(List<ProductSku> skus) {
        skus.forEach(sku -> {
            if (sku.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidException("sku.price-must-not-be-less-than-zero");
            }
            if (sku.getOriginalPrice() == null || sku.getOriginalPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidException("sku.original_price-must-not-be-less-than-zero");
            }
        });
    }

    /**
     * Normalizes the input fields of the given {@link Product} object.
     *
     * @param product the {@link Product} object to be normalized
     * @return the normalized {@link Product} object
     */
    public Product normalizeInput(Product product) {
        product.setName(normalizeInputString(product.getName()));
        product.setDescription(normalizeInputString(product.getDescription()));
        return product;
    }

    /**
     * Normalizes the given input string by replacing multiple spaces with a single space and trimming.
     *
     * @param input the input string to be normalized
     * @return the normalized string
     */
    public String normalizeInputString(String input) {
        return input.replaceAll("\\s{2,}", " ").trim();
    }
}

