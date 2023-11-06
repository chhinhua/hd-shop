package com.hdshop.service.sms;

public interface SmsService {
    void sendSms(final String toPhoneNumber, final String messageBody);
}
