package com.hdshop.service.product;

import com.hdshop.entity.product.Product;
import com.hdshop.entity.product.ProductSku;

import java.util.List;

public interface ProductSkuService {
    List<ProductSku> addProductSkus(final Product product, final List<ProductSku> productSkus);
}
