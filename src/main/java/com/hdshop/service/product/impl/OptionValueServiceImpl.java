package com.hdshop.service.product.impl;

import com.hdshop.entity.OptionValue;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.OptionValueRepository;
import com.hdshop.service.product.OptionValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OptionValueServiceImpl implements OptionValueService {
    private final OptionValueRepository optionValueRepository;
    private final MessageSource messageSource;

    @Override
    public Optional<OptionValue> findByValueNameAndProductId(String valueName, Long productId) {
         return optionValueRepository.findByValueNameAndOption_ProductProductId(valueName, productId);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
