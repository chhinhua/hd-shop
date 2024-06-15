package com.duck.repository;

import com.duck.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.product.productId = :product_id AND (:star IS NULL OR r.stars = :star)")
    Page<Review> findByProduct(
            @Param("product_id") Long productId,
            @Param("star") Integer star,
            Pageable pageable);

    List<Review> findAllByProduct_ProductId(Long productId);

    boolean existsByOrderItem_Id(Long itemId);

    @Query("SELECT CASE WHEN COUNT(r) = 0 AND oi.order.status = 'DELIVERED' THEN true ELSE false END " +
            "FROM OrderItem oi " +
            "LEFT JOIN oi.review r " +
            "WHERE oi.id = :itemId")
    boolean checkHasReview(@Param("itemId") Long itemId);
}
