package com.hdshop.service.product.impl;

import com.hdshop.entity.product.OptionValue;
import com.hdshop.entity.product.Product;
import com.hdshop.repository.product.OptionValueRepository;
import com.hdshop.service.product.OptionValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OptionValueServiceImpl implements OptionValueService {
    private final OptionValueRepository optionValueRepository;

    @Override
    public Optional<OptionValue> getByValueNameAndProductId(String valueName, Long productId) {
         return optionValueRepository.findByValueNameAndOption_ProductProductId(valueName, productId);
    }
}
