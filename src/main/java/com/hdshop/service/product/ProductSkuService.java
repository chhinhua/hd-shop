package com.hdshop.service.product;

import com.hdshop.entity.Product;
import com.hdshop.entity.ProductSku;

import java.util.List;

public interface ProductSkuService {
    List<ProductSku> saveOrUpdateSkus(final Long productId, final List<ProductSku> skus);

    List<ProductSku> saveSkusFromProduct(final Product product);
}
