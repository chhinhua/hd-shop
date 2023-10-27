package com.hdshop.service.product;

import com.hdshop.entity.product.Product;
import com.hdshop.entity.product.ProductSku;

import java.util.List;

public interface ProductSkuService {
    List<ProductSku> saveOrUpdateSkus(final Long productId, final List<ProductSku> skus);

    List<ProductSku> saveSkusFromProduct(final Product product);
}
