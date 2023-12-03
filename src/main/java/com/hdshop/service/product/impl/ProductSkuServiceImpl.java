package com.hdshop.service.product.impl;

import com.hdshop.entity.OptionValue;
import com.hdshop.entity.Product;
import com.hdshop.entity.ProductSku;
import com.hdshop.exception.InvalidException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.OptionValueRepository;
import com.hdshop.repository.ProductRepository;
import com.hdshop.repository.ProductSkuRepository;
import com.hdshop.service.product.OptionValueService;
import com.hdshop.service.product.ProductSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSkuServiceImpl implements ProductSkuService {
    private final ProductSkuRepository productSkuRepository;
    private final OptionValueService optionValueService;
    private final ProductRepository productRepository;
    private final MessageSource messageSource;
    private final OptionValueRepository optionValueRepository;

    /**
     * Save or update list productSku information
     *
     * @param productId
     * @param skus
     * @return List ProductSku
     * @date 27-10-2023
     */
    @Override
    @Transactional
    public List<ProductSku> saveOrUpdateSkus(Long productId, List<ProductSku> skus) {
        Product product = getProductById(productId);

        List<ProductSku> saveProductSkus = new ArrayList<>();
        for (ProductSku productSku : skus) {
            List<String> valueNames = getValueNames(productSku.getOptionValues());
            Optional<ProductSku> existingSku = productSkuRepository.findByProductIdAndValueNames(
                    productId, valueNames, valueNames.size()
            );

            if (existingSku.isPresent()) {
                updateExistingSku(existingSku.get(), productSku);
                productSkuRepository.flush();
                saveProductSkus.add(existingSku.get());
            } else {
                ProductSku newProductSku = createNewProductSku(productSku, product);
                saveProductSkus.add(newProductSku);
            }
        }

        return saveProductSkus;
    }

    @Override
    public List<ProductSku> saveSkusFromProduct(Product product) {
        List<ProductSku> savedSkus = new ArrayList<>();

        for (ProductSku sku : product.getSkus()) {
            sku.setProduct(product);

            List<OptionValue> optionValues = getOptionValuesForSku(sku, product);

            sku.setOptionValues(optionValues);
            savedSkus.add(productSkuRepository.save(sku));
        }

        return savedSkus.stream().toList();
    }

    @Override
    public ProductSku findByProductIdAndValueNames(Long productId, List<String> valueNames) {
        if (productId == null) {
            throw new InvalidException("product-id-must-not-be-null");
        }
        if (valueNames.isEmpty()) {
            throw new InvalidException("value-names-must-not-be-empty");
        }
        ProductSku sku;
        try {
            sku = productSkuRepository.findByProductIdAndValueNames(productId, valueNames, valueNames.size()).get();
        } catch (Exception e) {
            throw new ResourceNotFoundException(getMessage("sku-not-found-please-choose-anorther-style"));
        }
        return sku;
    }

    private List<String> getValueNames(List<OptionValue> values) {
        return values.stream()
                .map(value -> value.getValueName())
                .collect(Collectors.toList());
    }

    private List<OptionValue> getOptionValuesForSku(ProductSku sku, Product product) {
        List<OptionValue> optionValues = new ArrayList<>();

        for (OptionValue value : sku.getOptionValues()) {
            OptionValue newOptionValue = getOrCreateOptionValue(value, product);

            optionValues.add(newOptionValue);
        }

        return optionValues;
    }

    private OptionValue getOrCreateOptionValue(OptionValue value, Product product) {
        Optional<OptionValue> newOptionValue = optionValueService
                .findByValueNameAndProductId(value.getValueName(), product.getProductId());

        return newOptionValue.orElse(value); // Return existing or follow new
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    private Optional<ProductSku> getProductSkuBySkuAndProductId(String sku, Long productId) {
        return productSkuRepository.findBySkuAndProduct_ProductId(sku, productId);
    }

    private void updateExistingSku(ProductSku existingProductSku, ProductSku newProductSku) {
        if (newProductSku.getPrice() != null && newProductSku.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            existingProductSku.setPrice(newProductSku.getPrice());
        }
    }

    private ProductSku createNewProductSku(ProductSku productSku, Product product) {
        List<OptionValue> valueList = getExistingOptionValues(productSku.getOptionValues(), product.getProductId());

        productSku.setProduct(product);
        productSku.setOptionValues(valueList);

        return productSkuRepository.save(productSku);
    }

    private List<OptionValue> getExistingOptionValues(List<OptionValue> optionValues, Long productId) {
        List<OptionValue> valueList = new ArrayList<>();

        for (OptionValue value : optionValues) {
            OptionValue existingOptionValue = optionValueService.findByValueNameAndProductId(value.getValueName(), productId).get();
            valueList.add(existingOptionValue);
        }

        return valueList;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
