package com.hdshop.service.product;

import com.hdshop.entity.OptionValue;

import java.util.Optional;

public interface OptionValueService {
    Optional<OptionValue> findByValueNameAndProductId(final String valueName, final Long productId);
}
