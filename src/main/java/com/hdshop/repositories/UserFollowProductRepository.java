package com.hdshop.repositories;

import com.hdshop.entities.UserFollowProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowProductRepository extends JpaRepository<UserFollowProduct, Long> {
}
