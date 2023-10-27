package com.hdshop.service.product;

import com.hdshop.entity.product.OptionValue;

import java.util.Optional;

public interface OptionValueService {
    Optional<OptionValue> getByOptionNameAndProductId(final String valueName, final Long productId);
}
