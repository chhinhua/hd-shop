package com.hdshop.repository;

import com.hdshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByProduct_ProductIdAndUser_Username(Long productId, String username);
}
