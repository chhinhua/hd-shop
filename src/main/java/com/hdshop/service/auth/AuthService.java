package com.hdshop.service.auth;

import com.hdshop.dto.auth.LoginDTO;
import com.hdshop.dto.auth.LoginResponse;
import com.hdshop.dto.auth.RegisterDTO;
import com.hdshop.dto.auth.VerifyOtpRequest;

public interface AuthService {
    String register(final RegisterDTO registerDTO);

    LoginResponse login(final LoginDTO loginDTO);

    String sendOTP_ByEmail(final String email);

    String sendOTP_ByUsername(final String username);

    String verifyOTP_ByEmail(final VerifyOtpRequest otpRequest);
}
