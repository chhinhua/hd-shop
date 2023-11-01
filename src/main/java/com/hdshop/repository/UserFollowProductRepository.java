package com.hdshop.repository;

import com.hdshop.entity.UserFollowProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowProductRepository extends JpaRepository<UserFollowProduct, Long> {
}
