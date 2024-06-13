package com.hdshop.repository;

import com.hdshop.entity.Category;
import com.hdshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Boolean existsCategoryByName(String name);

    Boolean existsCategoryBySlug(String slug);

    Boolean existsCategoryByParentId(Long parentId);

    Boolean existsCategoryById(Long id);

    Optional<Category> findBySlug(String slug);

    Optional<Category> findByName(String name);

    @Query("SELECT c " +
            "FROM Category c " +
            "LEFT JOIN c.products p " +
            "WHERE c.isDeleted = false " +
            "AND (:key IS NULL OR LOWER(c.name) LIKE %:key%) " +
            "GROUP BY c.id " +
            "ORDER BY " +
            "CASE WHEN 'id:asc' IN :sortCriteria THEN c.id END ASC, " +
            "CASE WHEN 'id:desc' IN :sortCriteria THEN c.id END DESC, " +
            "CASE WHEN 'name:asc' IN :sortCriteria THEN c.name END ASC, " +
            "CASE WHEN 'prod_count:asc' IN :sortCriteria THEN COUNT(p) END ASC, " +
            "CASE WHEN 'prod_count:desc' IN :sortCriteria THEN COUNT(p) END DESC "
    )
    Page<Category> filter(
            @Param("key") String key,
            @Param("sortCriteria") List<String> sortCriteria,
            Pageable pageable
    );
}
