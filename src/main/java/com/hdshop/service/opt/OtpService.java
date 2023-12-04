package com.hdshop.service.opt;

import org.springframework.scheduling.annotation.Async;

public interface OtpService {
    @Async
    void sendOTP(final String email, final String otp);
}
