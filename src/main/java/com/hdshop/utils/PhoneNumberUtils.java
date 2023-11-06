package com.hdshop.utils;

public class PhoneNumberUtils {

    /**
     * Thay đầu 0 thành +84
     * @param phoneNumber
     */
    public static String convertToInternationalFormat(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+84" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}
