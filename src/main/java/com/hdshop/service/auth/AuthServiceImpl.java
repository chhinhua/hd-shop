package com.hdshop.service.auth;

import com.hdshop.dto.auth.LoginDTO;
import com.hdshop.dto.auth.RegisterDTO;
import com.hdshop.entity.Role;
import com.hdshop.entity.User;
import com.hdshop.exception.APIException;
import com.hdshop.repository.RoleRepository;
import com.hdshop.repository.UserRepository;
import com.hdshop.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Chhin Hua
 * @date 29-10-2023
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Registers a new user based on the provided registration data.
     *
     * @param registerDTO The data containing user registration information.
     * @return A success message indicating that the user has been registered.
     * @throws APIException If the provided username or email already exist in the database, an exception is thrown with a Bad Request status and a specific message.
     */
    @Override
    public String register(RegisterDTO registerDTO) {
        // Check if the username already exists in the database
        if (userRepository.existsUserByUsername(registerDTO.getUsername())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Username is already exists!");
        }

        // Check if the email already exists in the database
        if (userRepository.existsUserByEmail(registerDTO.getEmail())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Email is already exists!");
        }

        // Create a new User object and populate it with the provided registration data
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        // Set the user's role(s)
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        // Save the user to the database
        userRepository.save(user);

        // Return a success message
        return "User registered successfully!";
    }

    /**
     * Handles user login based on the provided login credentials.
     *
     * @param loginDTO The data containing user login information.
     * @return The JWT token generated upon successful authentication.
     */
    @Override
    public String login(LoginDTO loginDTO) {
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

        // Return the generated JWT token
        return token;
    }
}
