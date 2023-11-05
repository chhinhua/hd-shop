package com.hdshop.service.user;

import com.hdshop.dto.user.UserDTO;

public interface UserService {
    UserDTO getUserById(final Long id);

    void changePassword(final String newPassword);
}
