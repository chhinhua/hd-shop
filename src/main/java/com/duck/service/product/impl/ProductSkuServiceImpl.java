package com.duck.service.product.impl;

import com.duck.entity.OptionValue;
import com.duck.entity.Product;
import com.duck.entity.ProductSku;
import com.duck.exception.InvalidException;
import com.duck.exception.ResourceNotFoundException;
import com.duck.repository.ProductRepository;
import com.duck.repository.ProductSkuRepository;
import com.duck.service.product.OptionValueService;
import com.duck.service.product.ProductSkuService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductSkuServiceImpl implements ProductSkuService {
    ProductSkuRepository productSkuRepository;
    OptionValueService optionValueService;
    ProductRepository productRepository;
    MessageSource messageSource;

    @Override
    @Transactional
    public void saveSkusProductCreation(Product product) {
        List<ProductSku> savedSkus = new ArrayList<>();
        for (ProductSku sku : product.getSkus()) {
            List<OptionValue> optionValues = getOptionValuesForSku(sku, product);
            sku.setProduct(product);
            sku.setOptionValues(optionValues);
            sku.setSold(0);
            sku.setQuantityAvailable(sku.getQuantity());
            sku.setPercentDiscount(product.getPercentDiscount() == null ? 0 : product.getPercentDiscount());
            savedSkus.add(productSkuRepository.save(sku));
        }
        savedSkus.stream().toList();
    }

    /**
     * Retrieves or creates OptionValue instances for a given ProductSku and Product.
     *
     * @param sku The ProductSku containing the option values to process
     * @param product The Product associated with the SKU
     * @return A list of OptionValue instances, either existing or newly created
     */
    private List<OptionValue> getOptionValuesForSku(ProductSku sku, Product product) {
        List<OptionValue> optionValues = new ArrayList<>();
        for (OptionValue value : sku.getOptionValues()) {
            OptionValue newOptionValue = getOrCreateOptionValue(value, product);
            optionValues.add(newOptionValue);
        }
        return optionValues;
    }

    @Override
    @Transactional
    public void saveOrUpdateListSkus(Long productId, List<ProductSku> skus) {
        Product product = findProductById(productId);
        skus.stream()
                .map(sku -> saveOrUpdateSku(product, sku))
                .collect(Collectors.toList());
    }

    /**
     * Saves a new {@link ProductSku} or updates an existing one based on product and option values.
     *
     * @param product The associated {@link Product}
     * @param sku The {@link ProductSku} to save or update
     * @return The saved or updated {@link ProductSku}
     */
    private ProductSku saveOrUpdateSku(Product product, ProductSku sku) {
        List<String> valueNames = getValueNames(sku.getOptionValues());
        return productSkuRepository.findByProductIdAndValueNames(product.getProductId(), valueNames, valueNames.size())
                .map(existingSku -> updateExistingSku(existingSku, sku))
                .orElseGet(() -> createNewSku(sku, product));
    }

    /**
     * Updates an existing {@link ProductSku} with new values if they differ.
     *
     * @param existingSku The existing {@link ProductSku} to update
     * @param newSku The new {@link ProductSku} containing updated values
     * @return The updated {@link ProductSku}
     */
    private ProductSku updateExistingSku(ProductSku existingSku, ProductSku newSku) {
        Integer oldPercentDiscountVal = existingSku.getPercentDiscount();
        Integer newPercentDiscountVal = newSku.getProduct().getPercentDiscount();

        if (!Objects.equals(oldPercentDiscountVal, newPercentDiscountVal)) {
            existingSku.setPercentDiscount(newPercentDiscountVal);
        }
        if (newSku.getOriginalPrice() != existingSku.getOriginalPrice()) {
            existingSku.setOriginalPrice(newSku.getOriginalPrice());
        }
        if (newSku.getPrice() != existingSku.getPrice()) {
            existingSku.setPrice(newSku.getPrice());
        }

        return productSkuRepository.save(existingSku);
    }

    /**
     * Creates and saves a new {@link ProductSku} with updated details.
     *
     * This method:
     * - Sets the sold quantity to 0
     * - Sets the available quantity to the initial quantity
     * - Updates the percent discount from the product
     * - Associates the SKU with the given product
     * - Updates option values based on existing product option values
     *
     * @param productSku The {@link ProductSku} to be created and updated
     * @param product The associated {@link Product}
     * @return The newly created and saved {@link ProductSku}
     */
    private ProductSku createNewSku(ProductSku productSku, Product product) {
        List<OptionValue> valueList = getExistingOptionValues(
                productSku.getOptionValues(),
                product.getProductId()
        );
        productSku.setSold(0);
        productSku.setQuantityAvailable(productSku.getQuantity());
        productSku.setPercentDiscount(product.getPercentDiscount());
        productSku.setProduct(product);
        productSku.setOptionValues(valueList);

        return productSkuRepository.save(productSku);
    }

    private List<String> getValueNames(List<OptionValue> values) {
        return values.stream()
                .map(OptionValue::getValueName)
                .collect(Collectors.toList());
    }

    @Override
    public ProductSku findByProductIdAndValueNames(Long productId, List<String> valueNames) {
        if (productId == null) {
            throw new InvalidException("product-id-must-not-be-null");
        }
        if (valueNames.isEmpty()) {
            throw new InvalidException("value-names-must-not-be-empty");
        }
        try {
            return productSkuRepository.findByProductIdAndValueNames(productId, valueNames,valueNames.size()).get();
        } catch (Exception e) {
            throw new ResourceNotFoundException(getMessage("sku-not-found-please-choose-anorther-style"));
        }
    }

    @Override
    public ProductSku findById(Long skuId) {
        return productSkuRepository.findById(skuId).orElseThrow(
                () -> new ResourceNotFoundException(getMessage("sku-not-found"))
        );
    }

    /**
     * Retrieves an existing OptionValue or returns the provided one if not found.
     *
     * @param value The OptionValue to search for or potentially create
     * @param product The Product associated with the OptionValue
     * @return An existing OptionValue if found, otherwise the provided value
     */
    private OptionValue getOrCreateOptionValue(OptionValue value, Product product) {
        Optional<OptionValue> newOptionValue = optionValueService
                .findByValueNameAndProductId(
                        value.getValueName(),
                        product.getProductId()
                );
        return newOptionValue.orElse(value); // Return existing or follow new
    }

    /**
     * Retrieves existing OptionValue instances for a list of OptionValues and a product ID.
     *
     * @param optionValues The list of OptionValues to find existing instances for
     * @param productId The ID of the product associated with the OptionValues
     * @return A list of existing OptionValue instances
     */
    private List<OptionValue> getExistingOptionValues(List<OptionValue> optionValues, Long productId) {
        List<OptionValue> valueList = new ArrayList<>();
        for (OptionValue value : optionValues) {
            OptionValue existingOptionValue = optionValueService
                    .findByValueNameAndProductId(
                            value.getValueName(),
                            productId
                    ).get();
            valueList.add(existingOptionValue);
        }
        return valueList;
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Product", "id", productId)
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
