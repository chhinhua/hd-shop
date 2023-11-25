package com.hdshop.repository;

import com.hdshop.entity.Order;
import com.hdshop.utils.EnumOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> getOrdersByUser_UsernameOrderByCreatedDateDesc(String username);

    List<Order> getOrdersByUserIdOrderByCreatedDateDesc(Long userId);

    List<Order> findAllByUser_UsernameAndIsDeletedIsFalseOrderByCreatedDateDesc(String username);

    Optional<Order> findByVnpTxnRef(String vnp_TxnRef);

    List<Order> findByStatusOrderByCreatedDate(EnumOrderStatus status);

    List<Order> findAllByStatusAndUser_UsernameAndIsDeletedIsFalseOrderByCreatedDateDesc(EnumOrderStatus status, String username);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.user u JOIN o.orderItems oi " +
            "WHERE o.isDeleted = false " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND ((:key IS NOT NULL AND (LOWER(u.name) LIKE %:key% " +
            "OR LOWER(u.username) LIKE %:key% " +
            "OR LOWER(oi.product.name) LIKE %:key%)) " +
            "OR (:key IS NULL)) " +
            "ORDER BY " +
            "CASE WHEN 'id:asc' IN :sortCriteria THEN o.id END ASC, " +
            "CASE WHEN 'id:desc' IN :sortCriteria THEN o.id END DESC ")
    Page<Order> filter(
            @Param("status") EnumOrderStatus status,
            @Param("key") String key,
            @Param("sortCriteria") List<String> sortCriteria,
            Pageable pageable);

    Long countByStatus(EnumOrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND DATE(o.createdDate) = :date")
    Long countByDate(
            @Param("date") LocalDate date,
            @Param("status") EnumOrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND MONTH(o.createdDate) = :month AND YEAR(o.createdDate) = :year")
    Long countByMonthAndYear(
            @Param("month") int month,
            @Param("year") int year,
            @Param("status") EnumOrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND YEAR(o.createdDate) = :year")
    Long countByYear(
            @Param("year") int year,
            @Param("status") EnumOrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE YEAR(o.createdDate) = :year AND MONTH(o.createdDate) = :month AND o.status = 'DELIVERED'")
    Long getMonthlyOrderComplete(@Param("month") int month, @Param("year") int year);
}
