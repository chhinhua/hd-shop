package com.hdshop.repository;

import com.hdshop.entity.UserFollowProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFollowProductRepository extends JpaRepository<UserFollowProduct, Long> {
    Optional<UserFollowProduct> findByUser_UsernameAndProduct_ProductId(String username, Long productId);
}
