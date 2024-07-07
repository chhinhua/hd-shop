package com.duck.utils;

import com.duck.exception.InvalidException;
import com.duck.utils.enums.EOrderStatus;
import com.duck.utils.enums.EPaymentType;
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

    public EPaymentType getPaymentType(String input) {
        if ("VN_PAY".equals(input)) {
            return EPaymentType.VN_PAY;
        } else if ("COD".equals(input)) {
            return EPaymentType.COD;
        } else {
            throw new InvalidException(getMessage("payment-type-not-supported"));
        }
    }

    public EOrderStatus getOrderStatus(String value) {
        return switch (value) {
            case "Chờ thanh toán" -> EOrderStatus.WAIT_FOR_PAY;
            case "Đã đặt hàng" -> EOrderStatus.ORDERED;
            case "Đang xử lý" -> EOrderStatus.PROCESSING;
            case "Đang giao" -> EOrderStatus.SHIPPING;
            case "Đã giao" -> EOrderStatus.DELIVERED;
            case "Đã hủy" -> EOrderStatus.CANCELED;
            default -> throw new InvalidException(getMessage("error-fetching-order-status-information"));
        };
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

    public static String replaceVietnameseCharacters(String input) {
        String replacedString = input
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D");
        return replacedString;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
