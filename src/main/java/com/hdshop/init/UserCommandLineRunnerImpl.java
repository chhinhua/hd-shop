package com.hdshop.init;

import com.hdshop.entity.Address;
import com.hdshop.entity.Role;
import com.hdshop.entity.User;
import com.hdshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserCommandLineRunnerImpl implements CommandLineRunner {
    private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

    public UserCommandLineRunnerImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

    /**
     * Init a new User and save to database
     * Create sample user
     * @date 05-10-2023
     * @return
     */
    @Override
    public void run(String... args) throws Exception {
		//createXampleUser();
	}

	private void createXampleUser() {
		User admin = new User();
		admin.setUsername("admin");
		admin.setPassword(passwordEncoder.encode("admin"));
		admin.setFirstName("admin");
		admin.setLastName("1");
		admin.setEmail("admin@gmail.com");
		admin.setId_card("123456789");
		admin.setPhoneNumber("123456789");
		admin.setGender("Male");
		admin.setAvatarUrl("avatar-url");

		// create new address
		Address adminAddress = new Address();
		adminAddress.setUser(admin);
		adminAddress.setFullName("Admin");
		adminAddress.setCity("TP. Hồ Chí Minh");
		adminAddress.setDistrict("TP. Thủ Đức");
		adminAddress.setWard("P. Linh Xuân");
		adminAddress.setPhoneNumber("0326474614");
		adminAddress.setOrderDetails("162/9 Đường số 8");
		adminAddress.setIsDefault(true);

		admin.getAddresses().add(adminAddress);

		// Tạo và thiết lập các đối tượng Role
		Role role_user = new Role();
		role_user.setName("ROLE_USER");

		Role role_admin = new Role();
		role_admin.setName("ROLE_ADMIN");

		Set<Role> roles = new HashSet<>();
		roles.add(role_user);
		roles.add(role_admin);

		admin.setRoles(roles);

		userRepository.save(admin);
	}
}
