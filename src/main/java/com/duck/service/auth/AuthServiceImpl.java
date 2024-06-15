package com.duck.service.auth;

import com.duck.dto.auth.*;
import com.duck.dto.user.UserDTO;
import com.duck.entity.Role;
import com.duck.entity.User;
import com.duck.exception.APIException;
import com.duck.exception.InvalidException;
import com.duck.exception.ResourceNotFoundException;
import com.duck.repository.RoleRepository;
import com.duck.repository.UserRepository;
import com.duck.security.JwtTokenProvider;
import com.duck.service.opt.OtpService;
import com.duck.service.user.UserService;
import com.duck.utils.OtpUtils;
import com.duck.validator.UserValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chhin Hua
 * @date 29-10-2023
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtTokenProvider jwtTokenProvider;
    OtpService otpService;
    UserService userService;
    MessageSource messageSource;
    UserValidator userValidator;

    /**
     * Handles user login based on the provided login credentials.
     *
     * @param loginDTO The data containing user login information.
     * @return The JWT token generated upon successful authentication.
     */
    @Override
    public LoginResponse login(LoginDTO loginDTO) {
        try {
            // Authenticate user using provided credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsernameOrEmail(),
                            loginDTO.getPassword())
            );

            // Set the authenticated user's information in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate a JWT token for the authenticated user
            String token = jwtTokenProvider.generateToken(authentication);

            // Get user from token
            UserDTO user = userService.getByUsernameOrEmail(loginDTO.getUsernameOrEmail());

            // Check if user is enabled
            if (!user.getIsEnabled()) {
                throw new RuntimeException(getMessage("unverified-account"));
            }

            // check account is locked
            if (user.isLocked()) {
                throw new RuntimeException(getMessage("account-has-been-locked,-please-contact-admin-for-support"));
            }

            // follow jwtResponse object
            JwtAuthResponse jwtResponse = new JwtAuthResponse();
            jwtResponse.setAccessToken(token);

            return new LoginResponse(user, jwtResponse);
        } catch (BadCredentialsException exception) {
            throw new com.duck.exception.BadCredentialsException(getMessage("username-or-password-incorrect"));
        }
    }

    @Override
    public LoginResponse loginAdmin(LoginDTO loginDTO) {
        Optional<User> user = userRepository.findByUsernameOrEmail(loginDTO.getUsernameOrEmail(), loginDTO.getUsernameOrEmail());
        if (user.isPresent()) {
            if (user.get().getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
                throw new RuntimeException(getMessage("username-or-password-incorrect"));
            }
        }
        return login(loginDTO);
    }

    /**
     * Registers a new user based on the provided registration data.
     *
     * @param registerDTO The data containing user registration information.
     * @return A success message indicating that the user has been registered.
     * @throws APIException If the provided username or email already exist in the database, an exception is thrown with a Bad Request status and a specific message.
     */
    @Override
    @Transactional
    public String register(RegisterDTO registerDTO) {
        // validate
        userValidator.validateRegisterRequest(registerDTO);

        // generate OTP
        String OTP = OtpUtils.generateOTP();

        // save register information
        saveRegisterInfo(registerDTO, OTP);

        // send OTP
        String successMessage = getMessage(String.format("%s (%s) %s",
                getMessage("please-check-your-email"), registerDTO.getEmail(),
                getMessage("to-confirm-account")));
        return sendOTP(successMessage, registerDTO.getEmail(), OTP);
    }

    @Override
    public String verifyOTP_ByEmail(VerifyOtpRequest otpRequest) {
        // check already exists email
        User user = checkExistingUserByEmail(otpRequest.getEmail());

        // validate otp
        validateOtp(otpRequest.getOtp(), user);

        // active account
        user.setIsEnabled(true);
        user.setIsEmailActive(true);
        userRepository.save(user);

        return getMessage("auth-verify-successful");
    }

    @Override
    public String sendOTP_ByEmail(String email) {
        if (!isValidEmail(email)) {
            throw new InvalidException(getMessage("invalid-email-address"));
        }

        User user = checkExistingUserByEmail(email);

        String otp = OtpUtils.generateOTP();

        // save new otp for this user account
        user.setOtp(otp);
        user.setOtpCreatedTime(LocalDateTime.now());
        userRepository.save(user);

        // send OTP
        String successMessage = getMessage(String.format("%s. %s (%s).",
                getMessage("otp-send-successful"),
                getMessage("please-check-your-email"), email));
        return sendOTP(successMessage, email, otp);
    }

    @Override
    public String sendOTP_ByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("%s %s", getMessage("user-not-found-with-username-is"), username))
                );

        String otp = OtpUtils.generateOTP();

        // save new otp for this user account
        user.setOtp(otp);
        user.setOtpCreatedTime(LocalDateTime.now());
        userRepository.save(user);

        // send OTP
        String email = user.getEmail();
        String successMessage = getMessage(String.format("%s. %s (%s).",
                getMessage("otp-send-successful"),
                getMessage("please-check-your-email"), email));
        return sendOTP(successMessage, email, otp);
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    private User checkExistingUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("email-address-incorrect")));
    }

    private void saveRegisterInfo(RegisterDTO registerDTO, String otp) {
        // follow a new User object and populate it with the provided registration data
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setIsEmailActive(false);
        user.setIsEnabled(false);
        user.setIsPhoneActive(false);
        user.setOtp(otp);
        user.setOtpCreatedTime(LocalDateTime.now());

        // set the user's role(s)
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
    }

    private String sendOTP(String messsage, String email, String OTP) {
        try {
            otpService.sendOTP(email, OTP);
            return messsage;
        } catch (MailException e) {
            e.printStackTrace();
            throw new APIException(getMessage("otp-send-failed"));
        }
    }

    private void validateOtp(String otp, User user) {
        if (user.getOtp() == null || user.getOtpCreatedTime() == null) {
            throw new InvalidException(String.format("%s, %s"
                    ,getMessage("auth-verify-failed")
                    ,getMessage("please-try-again")));
        }

        if (!user.getOtp().equals(otp)) {
            throw new InvalidException(getMessage("otp-code-incorrect"));
        }

        if (Duration.between(user.getOtpCreatedTime(), LocalDateTime.now()).toMinutes() > 15) {
            throw new InvalidException(String.format("%s, %s"
                    ,getMessage("otp-code-has-expired")
                    ,getMessage("please-require-resend-otp")));
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
