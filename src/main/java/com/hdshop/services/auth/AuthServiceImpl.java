package com.hdshop.services.auth;

import com.hdshop.dtos.RegisterDTO;
import com.hdshop.entities.Role;
import com.hdshop.entities.User;
import com.hdshop.exceptions.APIException;
import com.hdshop.repositories.RoleRepository;
import com.hdshop.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    //private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public String register(RegisterDTO registerDTO) {

        if (userRepository.existsUserByUsername(registerDTO.getUsername())){
            throw new APIException(HttpStatus.BAD_REQUEST, "Username is already exists!");
        }
        if (userRepository.existsUserByEmail(registerDTO.getEmail())){
            throw new APIException(HttpStatus.BAD_REQUEST, "Email is already exists!");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        //user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        Set<Role> roles = new HashSet<Role>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return "User register successfully!";
    }
}
