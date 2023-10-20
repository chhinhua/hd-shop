package com.hdshop.service.product;

import com.hdshop.entity.product.Product;
import com.hdshop.entity.product.SkuValue;

import java.util.List;

public interface SkuValueService {
    List<SkuValue> addSkuValues(final Product product, final List<SkuValue> skuValues);
}
