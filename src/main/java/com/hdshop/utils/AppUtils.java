package com.hdshop.utils;

import com.hdshop.exception.InvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class AppUtils {
    @Autowired
    private MessageSource messageSource;

    @Value("${ghn.shop-id}")
    public static String ghn_shop_id;

    @Value("${ghn.token}")
    public static String ghn_token;

    public EnumPaymentType getPaymentType(String input) {
        if ("VN_PAY".equals(input)) {
            return EnumPaymentType.VN_PAY;
        } else if ("COD".equals(input)) {
            return EnumPaymentType.COD;
        } else {
            throw new InvalidException(getMessage("payment-type-not-supported"));
        }
    }

    public EnumOrderStatus getOrderStatus(String value) {
        if ("Chờ thanh toán".equals(value)) {
            return EnumOrderStatus.WAIT_FOR_PAY;
        } else if ("Đã đặt hàng".equals(value)) {
            return EnumOrderStatus.ORDERED;
        } else if ("Đang xử lý".equals(value)) {
            return EnumOrderStatus.PROCESSING;
        } else if ("Đang giao".equals(value)) {
            return EnumOrderStatus.SHIPPING;
        } else if ("Đã giao".equals(value)) {
            return EnumOrderStatus.DELIVERED;
        }else if ("Đã hủy".equals(value)) {
            return EnumOrderStatus.CANCELED;
        } else {
            throw new InvalidException(getMessage("error-fetching-order-status-information"));
        }
    }

    public static boolean isValidRating(Integer stars) {
        return stars != null && stars >= 1 && stars <= 5;
    }

    public static String decodeIfEncoded(String input) {
        // Kiểm tra xem chuỗi có phải đã được encode hay không
        if (input.contains("%")) {
            return URLDecoder.decode(input, StandardCharsets.UTF_8);
        }

        return input;
    }
    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
