package com.hdshop.service.auth;

import com.hdshop.dto.auth.LoginDTO;
import com.hdshop.dto.auth.LoginResponse;
import com.hdshop.dto.auth.RegisterDTO;
import com.hdshop.dto.auth.VerifyOtpRequest;

public interface AuthService {
    String register(final RegisterDTO registerDTO);

    LoginResponse login(final LoginDTO loginDTO);

    String sendCodeByPhoneNumber(final String phoneNumber);

    String sendOTP_ByEmail(final String email);

    String verifyOTP_ByEmail(final VerifyOtpRequest otpRequest);
}
