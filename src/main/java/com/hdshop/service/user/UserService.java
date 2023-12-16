package com.hdshop.service.user;

import com.hdshop.dto.user.ChangePassReq;
import com.hdshop.dto.user.UserDTO;
import com.hdshop.dto.user.UserProfile;
import com.hdshop.dto.user.UserResponse;
import com.hdshop.entity.User;

import java.security.Principal;
import java.util.List;

public interface UserService {
    UserDTO getById(final Long id);

    String changePassword(final ChangePassReq request, final Principal pricipal);

    String forgotPassword(final String email, final String newPassword);

    User findByUsername(final String username);

    UserDTO getByUsernameOrEmail(final String usernameOrEmail);

    UserDTO updateProfile(final UserProfile profile, final Principal principal);

    UserDTO updateProfileById(final UserProfile profile, final Long userId);

    UserDTO changeLockedStatus(final Long userId);

    UserResponse getAll(final int pageNo, final int pageSize);

    UserResponse filter(
            String key,
            List<String> sortCriteria,
            int pageNo,
            int pageSize,
            String roleName
    );
}
