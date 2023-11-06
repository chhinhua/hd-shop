package com.hdshop.service.sms;

import com.hdshop.utils.PhoneNumberUtils;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.ValidationRequest;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SmsServiceImpl implements SmsService {
    Dotenv dotenv = Dotenv.configure().load();
    public final String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    public final String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");
    public final String PHONE_NUMBER = dotenv.get("TWILIO_PHONE_NUMBER");

    @Override
    public void sendSms(String toPhoneNumber, String messageBody) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Hàm xóa 0 thêm +84
        String toConvertedPhoneNumber = PhoneNumberUtils
                .convertToInternationalFormat(toPhoneNumber);

        // Thêm số để viriTwilio
        //addNewCallerId(toConvertedPhoneNumber);

        // Gửi tin nhắn
        Message message = Message.creator(
                new PhoneNumber(toConvertedPhoneNumber),
                new PhoneNumber(PHONE_NUMBER),
                String.format(messageBody)
        ).create();

        System.out.println("Message SID: " + message.getSid());
    }

    private void addNewCallerId(String phoneNumber) {
        Date dateAddPhone = new Date();
        ValidationRequest validationRequest = ValidationRequest
                .creator(
                        new com.twilio.type.PhoneNumber(phoneNumber)
                )
                .setFriendlyName("user-" + dateAddPhone.toString())
                .create();
    }
}
