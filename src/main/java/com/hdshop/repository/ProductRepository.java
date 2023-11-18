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
    Boolean existsProductByName(String name);

    Boolean existsProductBySlug(String generatedSlug);

    Page<Product> findAllByIsActiveIsTrueOrderByProductIdDesc(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(:key IS NULL OR LOWER(p.name) LIKE %:key%) " +
            "AND (:cateNames IS NULL OR p.category.name IN :cateNames) " +
            "ORDER BY " +
            "CASE WHEN 'price:asc' IN :sortCriteria THEN p.price END ASC, " +
            "CASE WHEN 'price:desc' IN :sortCriteria THEN p.price END DESC, " +
            "CASE WHEN 'favorite:asc' IN :sortCriteria THEN p.favoriteCount END ASC, " +
            "CASE WHEN 'favorite:desc' IN :sortCriteria THEN p.favoriteCount END DESC, " +
            "CASE WHEN 'review:asc' IN :sortCriteria THEN p.numberOfRatings END ASC, " +
            "CASE WHEN 'review:desc' IN :sortCriteria THEN p.numberOfRatings END DESC, " +
            "CASE WHEN 'rating:asc' IN :sortCriteria THEN p.rating END ASC, " +
            "CASE WHEN 'rating:desc' IN :sortCriteria THEN p.rating END DESC, " +
            "p.productId DESC") // Sắp xếp theo id giảm dần làm mặc định
    Page<Product> searchSortAndFilterProducts(
            @Param("key") String key,
            @Param("cateNames") List<String> cateNames,
            @Param("sortCriteria") List<String> sortCriteria,
            Pageable pageable
    );
}
