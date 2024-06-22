package com.duck.service.email;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendSimpleMessage(String to, String subject, String text);
}
