package com.hdshop.services.auth;

import com.hdshop.dtos.RegisterDTO;

public interface AuthService {
    String register(final RegisterDTO registerDTO);
}
