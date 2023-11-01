package com.hdshop.service.product.impl;

import com.hdshop.entity.OptionValue;
import com.hdshop.entity.Product;
import com.hdshop.entity.ProductSku;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.ProductRepository;
import com.hdshop.repository.ProductSkuRepository;
import com.hdshop.service.product.OptionValueService;
import com.hdshop.service.product.ProductSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductSkuServiceImpl implements ProductSkuService {
    private final ProductSkuRepository productSkuRepository;
    private final OptionValueService optionValueService;
    private final ProductRepository productRepository;

    /**
     * Save or update list productSku information
     *
     * @param productId
     * @param skus
     * @return List ProductSku
     * @date 27-10-2023
     */
    @Override
    public List<ProductSku> saveOrUpdateSkus(Long productId, List<ProductSku> skus) {
        List<ProductSku> saveProductSkus = new ArrayList<>();

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "id", productId));

        for (ProductSku productSku : skus) {
            Optional<ProductSku> existingProductSku = productSkuRepository
                    .findBySkuAndProduct_ProductId(productSku.getSku(), productId);

            // check nếu có tồn tại thì set giá price thay đổi
            // nếu không tồn tại thì tạo mới rồi set optionValues cho nó
            // để khỏi bị tạo lại optionValues
            if (existingProductSku.isPresent()) {
                existingProductSku.get().setPrice(productSku.getPrice());
                saveProductSkus.add(existingProductSku.get());
            } else {
                List<OptionValue> valueList = new ArrayList<>();
                for (OptionValue value : productSku.getOptionValues()) {
                    // mặc định là đã tồn tại optionValue này vì logic trước đó đã lưu nó
                    OptionValue existingOptionValue = optionValueService
                            .getByValueNameAndProductId(value.getValueName(), productId)
                            .orElseThrow(
                                    () -> new ResourceNotFoundException(
                                            "OptionValue",
                                            "valueName and productId",
                                            value.getValueName()
                                    )
                            );

                    valueList.add(existingOptionValue);
                }

                // set values and save sku to database
                productSku.setProduct(product);
                productSku.setOptionValues(valueList);
                saveProductSkus.add(productSkuRepository.save(productSku));
            }
        }

        return saveProductSkus.stream().toList();
    }

    @Override
    public List<ProductSku> saveSkusFromProduct(Product product) {
        List<ProductSku> savedSkus = new ArrayList<>();

        for (ProductSku sku : product.getSkus()) {
            sku.setProduct(product);

            List<OptionValue> optionValues = new ArrayList<>();
            for (OptionValue value : sku.getOptionValues()) {
                Optional<OptionValue> newOptionValue = optionValueService
                        .getByValueNameAndProductId(value.getValueName(), product.getProductId());

                if (newOptionValue.isPresent()) {
                    optionValues.add(newOptionValue.get());
                }
            }

            // save sku within set values
            sku.setOptionValues(optionValues);
            savedSkus.add(productSkuRepository.save(sku));
        }

        return savedSkus.stream().toList();
    }
}
