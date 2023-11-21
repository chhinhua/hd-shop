package com.hdshop.repository;

import com.hdshop.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByUser_UsernameAndProduct_ProductId(String username, Long productId);

    Page<Follow> findAllByUser_UsernameOrderByLastModifiedDateDesc(String username, Pageable pageable);

    @Query("SELECT f.product.productId FROM Follow f WHERE f.user.username = :username AND f.isDeleted = false")
    List<Long> findProductIdsFollowedByUser(@Param("username") String username);
}
