package com.hdshop.services.user;

import com.hdshop.dtos.UserDTO;

public interface UserService {
    UserDTO getUserById(final Long id);
}
