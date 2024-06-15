package com.duck.service.auth;

import com.duck.dto.auth.LoginDTO;
import com.duck.dto.auth.LoginResponse;
import com.duck.dto.auth.RegisterDTO;
import com.duck.dto.auth.VerifyOtpRequest;

public interface AuthService {
    String register(final RegisterDTO registerDTO);

    LoginResponse login(final LoginDTO loginDTO);

    LoginResponse loginAdmin(final LoginDTO loginDTO);

    String sendOTP_ByEmail(final String email);

    String sendOTP_ByUsername(final String username);

    String verifyOTP_ByEmail(final VerifyOtpRequest otpRequest);
}
