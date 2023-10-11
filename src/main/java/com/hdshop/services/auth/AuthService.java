package com.hdshop.services.auth;

import com.hdshop.dtos.auth.RegisterDTO;

public interface AuthService {
    String register(final RegisterDTO registerDTO);
}
