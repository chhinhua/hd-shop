package com.hdshop.repository;

import com.hdshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByEmail(String email);

    boolean existsUserByEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsUserByPhoneNumber(String username);

    @Query(value = "SELECT email FROM users WHERE username = :username", nativeQuery = true)
    String getEmailByUsername(@Param("username") String username);
}
