package com.hdshop.service.user.impl;

import com.hdshop.dto.user.UserDTO;
import com.hdshop.entity.User;
import com.hdshop.exception.InvalidException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.UserRepository;
import com.hdshop.security.JwtTokenProvider;
import com.hdshop.service.user.UserService;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private String email;
    private String newPassword;

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User có id là " + id));
        return mapToDTO(user);
    }

    @Override
    public String changePasswordOfCurrentUser(String newPassword, Principal principal) {
        User user = userRepository.findByUsernameOrEmail(principal.getName(), principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));

        validateNewPassword(newPassword);

        return tryToChangeNewPassword(newPassword, user);
    }

    @Override
    public String changePasswordByUserEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("%s %s",
                        getMessage("user-not-found-with-email-is"), email)));

        validateNewPassword(newPassword);

        return tryToChangeNewPassword(newPassword, user);
    }

    @Override
    public UserDTO getUserByToken(String token) {
        User user = new User();

        if (jwtTokenProvider.validateToken(token)) {
            // get username from token
            String username = jwtTokenProvider.getUsername(token);

            // get user by username
            user = userRepository.findByUsernameOrEmail(username, username)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy người dùng với tên người dùng hoặc email là " + username)
                    );
        }

        return mapToDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Không tìm tấy tài khoản người dùng với tên tài khoản là " + username)
                );
        return mapToDTO(user);
    }

    @Override
    public UserDTO getUserByUsernameOrEmail(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(()-> new ResourceNotFoundException(getMessage("user-not-found")));

        return mapToDTO(user);
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

    private void validateNewPassword(String newPassword) {
        if (newPassword.length() < 8) {
            throw new InvalidException(String.format("%s %s",
                    getMessage("password-length"),
                    String.format(getMessage("cannot-be-less-than-n-characters"), 8)));
        }
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    private UserDTO mapToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
