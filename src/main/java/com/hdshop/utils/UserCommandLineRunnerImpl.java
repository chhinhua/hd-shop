package com.hdshop.utils;

import com.hdshop.entities.Role;
import com.hdshop.entities.User;
import com.hdshop.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class UserCommandLineRunnerImpl {
    private final UserRepository userRepository;

    public UserCommandLineRunnerImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create sample user
     * @author Chhin Hua
     * @date 05-10-2023
     * @return
     */
/*    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setUsername("user");
        user.setPassword("user");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setId_card("123456789");
        user.setPhoneNumber("123456789");
        user.setGender("Male");
        //user.getAddresses().add(new Address(""));
        //user.getAddresses().add();
        user.setAvatar("avatar-url");

        // Tạo và thiết lập các đối tượng Role
        Role role_user = new Role();
        role_user.setName("ROLE_USER");
        role_user.setCreateAt(new Date());

        Role role_admin = new Role();
        role_admin.setName("ROLE_ADMIN");
        role_admin.setCreateAt(new Date());

        Set<Role> roles = new HashSet<>();
        roles.add(role_user);
        roles.add(role_admin);

        user.setRoles(roles);

        userRepository.save(user);
    }*/
}
