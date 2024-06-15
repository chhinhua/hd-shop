package com.duck.utils;

import com.duck.exception.InvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class AppUtils {
    @Autowired
    private MessageSource messageSource;
    public static final String KEY_PREFIX_GET_ALL_PRODUCT = "all_products";
    public static final String KEY_PREFIX_GET_ALL_ORDER = "all_orders";
    public static final String KEY_PREFIX_GET_ALL_CATEGORY = "all_categories";
    public static final String KEY_PREFIX_GET_MY_ORDER = "my_orders";
    public static final String ROLE_ADMIN_NAME = "ROLE_ADMIN";
    public static final String ROLE_CLIENT_NAME = "ROLE_USER";

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
