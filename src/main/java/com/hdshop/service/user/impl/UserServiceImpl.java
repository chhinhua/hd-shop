package com.hdshop.service.user.impl;

import com.hdshop.dto.user.UserDTO;
import com.hdshop.entity.User;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.UserRepository;
import com.hdshop.security.JwtTokenProvider;
import com.hdshop.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageSource messageSource;

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User có id là " + id));
        return mapToDTO(user);
    }

    @Override
    public void changePassword(String newPassword) {

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

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    private UserDTO mapToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
