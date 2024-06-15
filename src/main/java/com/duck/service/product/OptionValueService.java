package com.duck.service.product;

import com.duck.entity.OptionValue;

import java.util.Optional;

public interface OptionValueService {
    Optional<OptionValue> findByValueNameAndProductId(final String valueName, final Long productId);
}
