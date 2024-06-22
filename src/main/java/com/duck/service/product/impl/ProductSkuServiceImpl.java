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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * 🎯Lưu hoặc cập nhật danh sách các phiên bản {@link ProductSku} liên quan đến một sản phẩm cụ thể.
     * <p>
     * Phương thức này lấy thông tin của {@link Product} dựa trên ID của nó, duyệt qua danh sách
     * các phiên bản {@link ProductSku} được cung cấp và xác định xem mỗi phiên bản có nên được cập nhật
     * hay tạo mới dựa trên sự tồn tại của phiên bản có các giá trị tùy chọn tương tự. Nếu có phiên bản
     * tương tự đã tồn tại, nó sẽ cập nhật thông tin của phiên bản đó. Nếu không tìm thấy phiên bản tương
     * tự, nó sẽ tạo mới một phiên bản {@link ProductSku} cho sản phẩm. Các phiên bản được cập nhật hoặc
     * tạo mới sẽ được lưu và trả về trong danh sách kết quả. ✅
     *
     * @param productId ID của {@link Product} mà các phiên bản {@link ProductSku} liên quan đến.
     * @param skus Danh sách các phiên bản {@link ProductSku} cần lưu hoặc cập nhật.
     * @return Danh sách các phiên bản {@link ProductSku} đã được lưu hoặc cập nhật.
     * @date: 27-10-2023 🗓
     * @author <a href="https://github.com/chhinhua">Chhin Hua</a> 👨‍💻
     */
    @Override
    public List<ProductSku> saveOrUpdateSkus(Long productId, List<ProductSku> skus) {
        // Retrieve entity
        Product product = getProductById(productId);
        List<ProductSku> saveProductSkus = new ArrayList<>();

        // Iterate through each sku in the provided list
        for (ProductSku productSku : skus) {
            List<String> valueNames = getValueNames(productSku.getOptionValues());
            Optional<ProductSku> existingSku = productSkuRepository.findByProductIdAndValueNames(
                    productId, valueNames, valueNames.size()
            );

            // If an existing sku is found, update it; otherwise, create a new sku
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

    /**
     * Saves the list of {@link ProductSku} instances associated with a {@link Product}.
     * <p>
     * This method iterates through the skus of the provided {@link Product}, assigns
     * the product reference, retrieves option values, sets percent discount if available,
     * and saves each SKU to the repository. The saved list of skus is returned.
     *
     * @param product The {@link Product} containing the skus to be saved.
     * @return A list of {@link ProductSku} instances that have been saved.
     */
    @Override
    public List<ProductSku> saveSkusFromProduct(Product product) {
        List<ProductSku> savedSkus = new ArrayList<>();
        for (ProductSku sku : product.getSkus()) {
            List<OptionValue> optionValues = getOptionValuesForSku(sku, product);
            sku.setProduct(product);
            sku.setOptionValues(optionValues);
            sku.setPercentDiscount(product.getPercentDiscount() == null ? 0 : product.getPercentDiscount());
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

    private List<String> getValueNames(List<OptionValue> values) {
        return values.stream()
                .map(OptionValue::getValueName)
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
                .findByValueNameAndProductId(
                        value.getValueName(),
                        product.getProductId()
                );
        return newOptionValue.orElse(value); // Return existing or follow new
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Product", "id", productId)
        );
    }

    private void updateExistingSku(ProductSku existingSku, ProductSku newSku) {
        if (newSku.getOriginalPrice() != null && newSku.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0) {
            existingSku.setOriginalPrice(newSku.getOriginalPrice());
        }
        if (newSku.getPrice() != null && newSku.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            existingSku.setPrice(newSku.getPrice());
        }
    }

    /**
     * Creates and saves a new {@link ProductSku} with updated product details.
     * <p>
     * This method updates the {@link ProductSku} with the following details:
     * <ul>
     *   <li>Resets the sold quantity to 0.</li>
     *   <li>Retains the current percent discount.</li>
     *   <li>Associates the {@link ProductSku} with the given {@link Product}.</li>
     *   <li>Updates the option values based on the existing option values of the product SKU and the product ID.</li>
     * </ul>
     *
     * @param productSku The original {@link ProductSku} to update.
     * @param product    The {@link Product} containing new product details.
     * @return The updated and saved {@link ProductSku}.
     */
    private ProductSku createNewProductSku(ProductSku productSku, Product product) {
        List<OptionValue> valueList = getExistingOptionValues(
                productSku.getOptionValues(),
                product.getProductId()
        );

        productSku.setSold(0);
        productSku.setPercentDiscount(productSku.getPercentDiscount());
        productSku.setProduct(product);
        productSku.setOptionValues(valueList);

        return productSkuRepository.save(productSku);
    }

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

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
