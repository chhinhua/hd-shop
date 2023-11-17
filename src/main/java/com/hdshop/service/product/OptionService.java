package com.hdshop.service.product;

import com.hdshop.entity.Option;
import com.hdshop.entity.Product;

import java.util.List;

public interface OptionService {
    List<Option> saveOrUpdateOptionsByProductId(Long productId, List<Option> options);
}
