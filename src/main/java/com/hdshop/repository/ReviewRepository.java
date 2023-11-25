package com.hdshop.repository;

import com.hdshop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByProduct_ProductIdAndUser_Username(Long productId, String username);

    Page<Review> findAllByProduct_ProductId(Long productId, Pageable pageable);
}
