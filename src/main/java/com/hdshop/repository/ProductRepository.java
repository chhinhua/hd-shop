package com.hdshop.repository;

import com.hdshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsProductBySlug(String generatedSlug);

    Page<Product> findAllByIsActiveIsTrue(Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE is_active = true AND is_selling = true ORDER BY RAND()", nativeQuery = true)
    Page<Product> findRandomProducts(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p WHERE " +
            "p.isActive = true " +
            "AND (:sell IS NULL OR p.isSelling = :sell) " +
            "AND (:key IS NULL OR LOWER(p.name) LIKE %:key%) " +
            "AND (:cateNames IS NULL OR p.category.name IN :cateNames) " +
            "AND (p.sold > 0 OR ( " +  // Check if sold is greater than 0 only when sorting by sold
                " 'sold:desc' NOT IN :sortCriteria " +
                "AND 'sold:asc' NOT IN :sortCriteria " +
            "))" +
            "ORDER BY " +
            "CASE WHEN 'random' IN :sortCriteria THEN RAND() END, " +
            "CASE WHEN 'id:asc' IN :sortCriteria THEN p.productId END ASC, " +
            "CASE WHEN 'id:desc' IN :sortCriteria THEN p.productId END DESC, " +
            "CASE WHEN 'price:asc' IN :sortCriteria THEN p.price END ASC, " +
            "CASE WHEN 'price:desc' IN :sortCriteria THEN p.price END DESC, " +
            "CASE WHEN 'favorite:asc' IN :sortCriteria THEN p.favoriteCount END ASC, " +
            "CASE WHEN 'favorite:desc' IN :sortCriteria THEN p.favoriteCount END DESC, " +
            "CASE WHEN 'review:asc' IN :sortCriteria THEN p.numberOfRatings END ASC, " +
            "CASE WHEN 'review:desc' IN :sortCriteria THEN p.numberOfRatings END DESC, " +
            "CASE WHEN 'sold:asc' IN :sortCriteria THEN p.sold END ASC, " +
            "CASE WHEN 'sold:desc' IN :sortCriteria THEN p.sold END DESC, " +
            "CASE WHEN 'available:asc' IN :sortCriteria THEN p.quantityAvailable END ASC, " +
            "CASE WHEN 'available:desc' IN :sortCriteria THEN p.quantityAvailable END DESC, " +
            "CASE WHEN 'rating:asc' IN :sortCriteria THEN p.rating END ASC, " +
            "CASE WHEN 'rating:desc' IN :sortCriteria THEN p.rating END DESC")
    Page<Product> filterProducts(
            @Param("sell") Boolean sell,
            @Param("key") String key,
            @Param("cateNames") List<String> cateNames,
            @Param("sortCriteria") List<String> sortCriteria,
            Pageable pageable
    );

    @Query("SELECT COUNT(oi) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE YEAR(oi.createdDate) = :year " +
            "AND MONTH(oi.createdDate) = :month " +
            "AND o.status = 'DELIVERED'")
    Long countMonthlySold(@Param("month") int month, @Param("year") int year);
}
