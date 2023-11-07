package com.hdshop.service.opt;

import com.hdshop.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtpServiceImpl implements OtpService {
    @Autowired
    private EmailService emailService;

    @Override
    public void sendOTP(String email, String otp) {
        String subject = "Duck Shop - Xác thực đăng ký tài khoản";
        String message = "Mã OTP của bạn là: " + otp + ", xác thực OTP để hoàn tất đăng ký tài khoản";
        emailService.sendSimpleMessage(email, subject, message);
    }
}
