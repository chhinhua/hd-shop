package com.hdshop.service.user.impl;

import com.hdshop.dto.user.UserDTO;
import com.hdshop.dto.user.UserProfile;
import com.hdshop.entity.User;
import com.hdshop.exception.InvalidException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.UserRepository;
import com.hdshop.service.user.UserService;
import com.hdshop.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User có id là " + id));
        return mapToDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm tấy tài khoản người dùng với tên tài khoản là " + username)
                );
        return mapToDTO(user);
    }

    @Override
    public UserDTO getUserByUsernameOrEmail(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));
        return mapToDTO(user);
    }

    @Override
    public UserDTO updateProfile(UserProfile profile, Principal principal) {
        String usernameOrEmail = principal.getName();

        // retrieve user from principal
        User updateUserProfile = userRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));

        // validate profile
        userValidator.validateUpdateProfile(profile, updateUserProfile);

        // set fields values
        setFieldValues(profile, updateUserProfile);

        // save the profile and return
        return mapToDTO(userRepository.save(updateUserProfile));
    }

    @Override
    public UserDTO updateProfileByUserId(UserProfile profile, Long userId) {
        // retrieve user by id
        User updateUserProfile = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(getMessage("user-not-found"), userId))
                );

        // validate profile
        userValidator.validateUpdateProfile(profile, updateUserProfile);

        // set fields values
        setFieldValues(profile, updateUserProfile);

        // save the profile and return
        return mapToDTO(userRepository.save(updateUserProfile));
    }

    @Override
    public UserDTO changeLockedStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        getMessage("user-not-found-with-id-is"), userId))
                );

        user.setLocked(!user.isLocked());

        return mapToDTO(userRepository.save(user));
    }

    @Override
    public String changePasswordOfCurrentUser(String newPassword, Principal principal) {
        User user = userRepository.findByUsernameOrEmail(principal.getName(), principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));

        userValidator.validatePassword(newPassword);

        return tryToChangeNewPassword(newPassword, user);
    }

    @Override
    public String changePasswordByUserEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("%s %s",
                        getMessage("user-not-found-with-email-is"), email)));

        userValidator.validatePassword(newPassword);

        return tryToChangeNewPassword(newPassword, user);
    }

    private void setFieldValues(UserProfile profile, User updateUserProfile) {
        updateUserProfile.setUsername(profile.getUsername());
        updateUserProfile.setName(profile.getName());
        updateUserProfile.setEmail(profile.getEmail());
        updateUserProfile.setPhoneNumber(profile.getPhoneNumber());
        updateUserProfile.setAvatarUrl(profile.getAvatarUrl());
        updateUserProfile.setGender(profile.getGender());
    }

    private String tryToChangeNewPassword(String newPassword, User user) {
        try {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return getMessage("password-changed-successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return getMessage("password-changed-failed");
        }
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    private UserDTO mapToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
