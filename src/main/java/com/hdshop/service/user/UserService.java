package com.hdshop.service.user;

import com.hdshop.dto.user.UserDTO;

import java.security.Principal;

public interface UserService {
    UserDTO getUserById(final Long id);

    String changePasswordOfCurrentUser(final String newPassword, final Principal pricipal);

    String changePasswordByUserEmail(final String email, final String newPassword);

    UserDTO getUserByToken(final String token);

    UserDTO getUserByUsername(final String username);

    UserDTO getUserByUsernameOrEmail(final String usernameOrEmail);
}
