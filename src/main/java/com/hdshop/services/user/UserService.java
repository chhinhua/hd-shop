package com.hdshop.services.user;

import com.hdshop.dtos.user.UserDTO;

public interface UserService {
    UserDTO getUserById(final Long id);
}
