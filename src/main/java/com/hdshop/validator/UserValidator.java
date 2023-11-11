package com.hdshop.validator;

import com.hdshop.dto.auth.RegisterDTO;
import com.hdshop.dto.user.UserProfile;
import com.hdshop.entity.User;
import com.hdshop.exception.APIException;
import com.hdshop.exception.InvalidException;
import com.hdshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final MessageSource messageSource;
    private final UserRepository userRepository;

    public void validateRegisterRequest(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String email = registerDTO.getEmail();
        String password = registerDTO.getPassword();

        // check invalid
        if (!isValidUsername(username)) {
            throw new InvalidException(getMessage("invalid-username"));
        }
        if (!isValidEmail(email)) {
            throw new InvalidException(getMessage("invalid-email-address"));
        }
        if (password.length() < 8) {
            throw new InvalidException((getMessage("password-length") + " " +
                    String.format(getMessage("cannot-be-less-than-n-characters"), "8")));
        }

        // check existing registration
        if (userRepository.existsUserByUsername(username)) {
            throw new APIException(HttpStatus.BAD_REQUEST, getMessage("username-already-exists"));
        }
        if (userRepository.existsUserByEmail(email)) {
            throw new APIException(HttpStatus.BAD_REQUEST, getMessage("email-already-exists"));
        }
    }

    public void validateUpdateProfile(UserProfile profile, User user) {
        String username = profile.getUsername();
        String email = profile.getEmail();
        String phoneNumber = profile.getPhoneNumber();

        // check invalid
        if (!isValidUsername(username)) {
            throw new InvalidException(getMessage("invalid-username"));
        }
        if (!isValidEmail(email)) {
            throw new InvalidException(getMessage("invalid-email-address"));
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new InvalidException(getMessage("invalid-phone-number"));
        }

        // check existing
        if (userRepository.existsUserByUsername(username) && !username.equals(user.getUsername())) {
            throw new InvalidException(getMessage("username-already-exists"));
        }
        if (userRepository.existsUserByEmail(email) && !email.equals(user.getEmail())) {
            throw new InvalidException(getMessage("email-already-used"));
        }
        if (userRepository.existsUserByPhoneNumber(phoneNumber) && !phoneNumber.equals(user.getPhoneNumber())) {
            throw new InvalidException(getMessage("phone-number-already-used"));
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9]{4,}$");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9]{10,11}$");
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
