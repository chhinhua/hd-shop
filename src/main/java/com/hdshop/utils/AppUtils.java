package com.hdshop.utils;

import com.hdshop.exception.InvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AppUtils {
    @Autowired
    private MessageSource messageSource;

    public EnumPaymentType getPaymentType(String input) {
        if ("VN_PAY".equals(input)) {
            return EnumPaymentType.VN_PAY;
        } else if ("COD".equals(input)) {
            return EnumPaymentType.COD;
        } else {
            throw new InvalidException(getMessage("payment-type-not-supported"));
        }
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
