package com.hdshop.validator;

import com.hdshop.dto.auth.RegisterDTO;
import com.hdshop.exception.APIException;
import com.hdshop.exception.InvalidException;
import com.hdshop.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator {
    private final MessageSource messageSource;
    private final UserRepository userRepository;

    public UserValidator(MessageSource messageSource, UserRepository userRepository) {
        this.messageSource = messageSource;
        this.userRepository = userRepository;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public void validateRegisterRequest(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String email = registerDTO.getEmail();
        String password = registerDTO.getPassword();

        // check input value
        if (!username.matches("^[a-zA-Z0-9]{4,}$")) {
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

    public boolean isValidEmail(String email) {
        // Regex cho định dạng email
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        // Kiểm tra sự khớp đúng
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

}
