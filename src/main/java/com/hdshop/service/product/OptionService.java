package com.hdshop.service.product;

import com.hdshop.entity.Option;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OptionService {
    List<Option> saveOrUpdateOptions(Long productId, List<Option> options);
}
