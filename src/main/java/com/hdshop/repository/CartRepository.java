package com.hdshop.repository;

import com.hdshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_Username(String username);

    Optional<Cart> findByUser_UsernameOrUser_Email(String username, String email);
}
