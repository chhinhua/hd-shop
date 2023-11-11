package com.hdshop.init;

import com.hdshop.entity.Address;
import com.hdshop.entity.Role;
import com.hdshop.entity.User;
import com.hdshop.repository.UserRepository;
import org.hibernate.mapping.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

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
		// Tạo và thiết lập các đối tượng Role
		Role role_user = new Role();
		role_user.setName("ROLE_USER");

		Role role_admin = new Role();
		role_admin.setName("ROLE_ADMIN");

		// create users
		User admin = createNewAdmin(role_admin);
		User user = createNewUser(role_user);

		// create address
		Address adminAddress = createAdminAddress(admin);
		Address userAddress = createUserAddress(user);

		// save
		userRepository.save(admin);
		userRepository.save(user);
	}

	private User createNewAdmin(Role role_admin) {
		User admin = new User();
		admin.setUsername("admin");
		admin.setPassword(passwordEncoder.encode("admin"));
		admin.setName("admin");
		admin.setEmail("admin@gmail.com");
		admin.setPhoneNumber("0444444444");
		admin.setGender("Male");
		admin.setAvatarUrl("avatar-url");
		admin.setIsPhoneActive(false);
		admin.setIsEmailActive(true);
		admin.setIsEnabled(true);
		admin.getRoles().add(role_admin);

		return admin;
	}

	private User createNewUser(Role role_user) {
		User user = new User();
		user.setUsername("user");
		user.setPassword(passwordEncoder.encode("user"));
		user.setName("user");
		user.setEmail("user@gmail.com");
		user.setPhoneNumber("0333333333");
		user.setGender("Male");
		user.setAvatarUrl("avatar-url");
		user.setIsPhoneActive(false);
		user.setIsEmailActive(true);
		user.setIsEnabled(true);
		user.getRoles().add(role_user);

		return user;
	}

	private Address createAdminAddress(User admin) {
		Address adminAddressddress = new Address();
		adminAddressddress.setUser(admin);
		adminAddressddress.setFullName("Admin");
		adminAddressddress.setCity("TP. Hồ Chí Minh");
		adminAddressddress.setDistrict("TP. Thủ Đức");
		adminAddressddress.setWard("P. Linh Xuân");
		adminAddressddress.setPhoneNumber("0326474614");
		adminAddressddress.setOrderDetails("162/9 Đường số 8");
		adminAddressddress.setIsDefault(true);
		admin.getAddresses().add(adminAddressddress);

		return adminAddressddress;
	}

	private Address createUserAddress(User user) {
		Address userAddress = new Address();
		userAddress.setUser(user);
		userAddress.setFullName("User");
		userAddress.setCity("TP. Hồ Chí Minh");
		userAddress.setDistrict("TP. Thủ Đức");
		userAddress.setWard("P. Linh Xuân");
		userAddress.setPhoneNumber("0326474614");
		userAddress.setOrderDetails("162/9 Đường số 8");
		userAddress.setIsDefault(true);
		user.getAddresses().add(userAddress);

		return userAddress;
	}
}
