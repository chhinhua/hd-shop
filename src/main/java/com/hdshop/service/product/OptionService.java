package com.hdshop.service.product;

import com.hdshop.entity.product.Option;
import com.hdshop.entity.product.Product;

import java.util.List;

public interface OptionService {
    List<Option> addOptions(final Product product, final List<Option> options);
}
