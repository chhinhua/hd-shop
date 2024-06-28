package com.duck.service.product;

import com.duck.entity.OptionValue;

import java.util.Optional;

public interface OptionValueService {

    /**
     * Finds an OptionValue by its value name and associated product ID.
     *
     * @param valueName The name of the option value to search for
     * @param productId The ID of the product associated with the option value
     * @return An Optional containing the OptionValue if found, or an empty Optional if not found
     * @throws
     */
    Optional<OptionValue> findByValueNameAndProductId(final String valueName, final Long productId);
}
