package com.duck.service.opt;

import com.duck.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class OtpServiceImpl implements OtpService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private MessageSource messageSource;

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    @Override
    public void sendOTP(String email, String otp) {
        String subject = "Duck Shop - Xác thực đăng ký tài khoản";
        String message = String.format("%s: %s", getMessage("your-otp-code-is"), otp);
        emailService.sendSimpleMessage(email, subject, message);
    }
}
