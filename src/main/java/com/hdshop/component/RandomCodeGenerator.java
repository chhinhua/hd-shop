package com.hdshop.component;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomCodeGenerator {
    public static String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }

        return code.toString();
    }
}
