package com.hdshop.service.product;

import com.hdshop.dto.product.OptionDTO;
import com.hdshop.entity.product.Option;
import com.hdshop.entity.product.OptionValue;
import com.hdshop.entity.product.Product;

import java.util.List;

public interface OptionValueService {
    List<OptionValue> addOptionValues(final Product product, final List<Option> options, final List<OptionDTO> optionDTOs);
}
