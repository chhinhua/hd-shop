package com.hdshop.init;

import com.hdshop.entity.Address;
import com.hdshop.entity.Role;
import com.hdshop.entity.User;
import com.hdshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserCommandLineRunnerImpl implements CommandLineRunner {
    private final UserRepository userRepository;

    public UserCommandLineRunnerImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
		User user = new User();
		user.setUsername("johndoe");
		user.setPassword("johndoee");
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("johndoe@example.com");
		user.setId_card("123456789");
		user.setPhoneNumber("123456789");
		user.setGender("Male");
		user.setAvatarUrl("avatar-url");

		// create new address
		Address address = new Address();
		address.setUser(user);
		address.setFullName("John doe");
		address.setCity("TP. Hồ Chí Minh");
		address.setDistrict("TP. Thủ Đức");
		address.setWard("P. Linh Xuân");
		address.setPhoneNumber("0326474614");
		address.setOrderDetails("162/9 Đường số 8");
		address.setDefault(true);

		user.getAddresses().add(address);

		// Tạo và thiết lập các đối tượng Role
		Role role_user = new Role();
		role_user.setName("ROLE_USER");

		Role role_admin = new Role();
		role_admin.setName("ROLE_ADMIN");

		Set<Role> roles = new HashSet<>();
		roles.add(role_user);
		roles.add(role_admin);

		user.setRoles(roles);

		userRepository.save(user);
	}
}
