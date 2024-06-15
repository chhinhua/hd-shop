package com.duck.utils;

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

    /**
     * Validate the phone number
     * @param phoneNumber
     * @return
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9]{10,11}$");
    }
}
