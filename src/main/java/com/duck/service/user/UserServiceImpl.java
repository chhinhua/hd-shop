package com.duck.service.user;

import com.duck.dto.user.ChangePassReq;
import com.duck.dto.user.UserDTO;
import com.duck.dto.user.UserProfile;
import com.duck.dto.user.UserResponse;
import com.duck.entity.User;
import com.duck.exception.InvalidException;
import com.duck.exception.ResourceNotFoundException;
import com.duck.repository.UserRepository;
import com.duck.validator.ProductValidator;
import com.duck.validator.UserValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    ModelMapper modelMapper;
    MessageSource messageSource;
    PasswordEncoder passwordEncoder;
    UserValidator userValidator;
    ProductValidator appValidator;

    @Override
    public UserDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User có id là " + id));
        return mapToDTO(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));
    }

    @Override
    public UserDTO getByUsernameOrEmail(String usernameOrEmail) {
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
    public UserDTO updateProfileById(UserProfile profile, Long userId) {
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
    public UserResponse getAll(int pageNo, int pageSize) {
        // follow Pageable instances
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        // get all users with role is user
        String roleName = "ROLE_USER";
        Page<User> userPage = userRepository.findAllByRoleName(roleName, pageable);

        // get content for page object
        List<User> userList = userPage.getContent();

        List<UserDTO> content = userList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // set data to the category response
        UserResponse userResponse = new UserResponse();
        userResponse.setContent(content);
        userResponse.setPageNo(userPage.getNumber() + 1);
        userResponse.setPageSize(userPage.getSize());
        userResponse.setTotalPages(userPage.getTotalPages());
        userResponse.setTotalElements(userPage.getTotalElements());
        userResponse.setLast(userPage.isLast());

        return userResponse;
    }

    @Override
    public UserResponse filter(String key, List<String> sortCriteria, int pageNo, int pageSize, String roleName) {
        // follow Pageable instances
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        String keyValue = key;
        if (key != null) {
            keyValue = appValidator.normalizeInputString(key);
        }
        Page<User> userPage = userRepository.filter(roleName, keyValue, sortCriteria, pageable);

        // get content for page object
        List<User> userList = userPage.getContent().stream()
                .filter(user -> user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_ADMIN")))
                .collect(Collectors.toList());


        List<UserDTO> content = userList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // set data to the user response
        UserResponse pageResponse = new UserResponse();
        pageResponse.setContent(content);
        pageResponse.setPageNo(userPage.getNumber() + 1);
        pageResponse.setPageSize(userPage.getSize());
        pageResponse.setTotalPages(userPage.getTotalPages());
        pageResponse.setTotalElements(userPage.getTotalElements());
        pageResponse.setLast(userPage.isLast());

        return pageResponse;
    }

    @Override
    public String changePassword(ChangePassReq request, Principal principal) {
        validateChangePassRequest(request);

        User user = userRepository
                .findByUsernameOrEmail(principal.getName(), principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));

        // check old password
        String oldPassword = request.getOldPassword();
        String savedEncodedPassword = user.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldPassword, savedEncodedPassword)) {
            throw new InvalidException(getMessage("old-password-incorrect"));
        }

        // change password
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        return getMessage("password-changed-successfully");
    }

    private void validateChangePassRequest(ChangePassReq request) {
        if (request.getOldPassword().isBlank()) {
            throw new InvalidException(getMessage("old-password-must-not-be-empty"));
        }
        if (request.getOldPassword().isBlank()) {
            throw new InvalidException(getMessage("new-password-must-not-be-empty"));
        }
        userValidator.validatePassword(request.getNewPassword());
    }

    @Override
    public String forgotPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("user-not-found-with-email-is") + " " + email)
        );

        userValidator.validatePassword(newPassword);

        // change password
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        return getMessage("password-changed-successfully");
    }

    private void setFieldValues(UserProfile profile, User updateUserProfile) {
        updateUserProfile.setUsername(profile.getUsername());
        updateUserProfile.setName(profile.getName());
        updateUserProfile.setEmail(profile.getEmail());
        updateUserProfile.setPhoneNumber(profile.getPhoneNumber());
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
