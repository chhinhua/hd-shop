package com.duck.service.product.impl;

import com.duck.entity.OptionValue;
import com.duck.repository.OptionValueRepository;
import com.duck.service.product.OptionValueService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OptionValueServiceImpl implements OptionValueService {
    OptionValueRepository optionValueRepository;
    MessageSource messageSource;

    @Override
    public Optional<OptionValue> findByValueNameAndProductId(String valueName, Long productId) {
         return optionValueRepository.findByValueNameAndOption_ProductProductId(valueName, productId);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
