package com.hdshop.service.auth;

import com.hdshop.dto.auth.LoginDTO;
import com.hdshop.dto.auth.LoginResponse;
import com.hdshop.dto.auth.RegisterDTO;

public interface AuthService {
    String register(final RegisterDTO registerDTO);

    LoginResponse login(final LoginDTO loginDTO);

    String sendCodeByPhoneNumber(final String phoneNumber);
}
