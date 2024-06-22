package com.duck.service.product;

import com.duck.entity.Product;
import com.duck.entity.ProductSku;

import java.util.List;

public interface ProductSkuService {
    List<ProductSku> saveOrUpdateSkus(final Long productId, final List<ProductSku> skus);

    List<ProductSku> saveSkusFromProduct(final Product product);

    ProductSku findByProductIdAndValueNames(final Long productId, final List<String> valueNames);

    ProductSku findById(final Long skuId);
}
