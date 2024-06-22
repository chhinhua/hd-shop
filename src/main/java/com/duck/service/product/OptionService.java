package com.duck.service.product;

import com.duck.entity.Option;

import java.util.List;

public interface OptionService {
    List<Option> saveOrUpdateOptions(Long productId, List<Option> options);
}
