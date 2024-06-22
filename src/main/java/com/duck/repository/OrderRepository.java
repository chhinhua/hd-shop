package com.duck.repository;

import com.duck.entity.Order;
import com.duck.utils.EOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> getOrdersByUser_UsernameOrderByCreatedDateDesc(String username);

    List<Order> getOrdersByUserIdOrderByCreatedDateDesc(Long userId);

    List<Order> findAllByUser_UsernameAndIsDeletedIsFalseOrderByCreatedDateDesc(String username);

    Optional<Order> findByVnpTxnRef(String vnp_TxnRef);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.id = :itemId")
    Optional<Order> findByItemId(Long itemId);

    List<Order> findAllByStatusAndUser_UsernameAndIsDeletedIsFalseOrderByCreatedDateDesc(EOrderStatus status, String username);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.user u JOIN o.orderItems oi " +
            "WHERE o.isDeleted = false " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND ((:key IS NOT NULL AND (LOWER(u.name) LIKE %:key% " +
            "OR LOWER(u.username) LIKE %:key% " +
            "OR LOWER(oi.product.name) LIKE %:key%)) " +
            "OR (:key IS NULL)) " +
            "ORDER BY " +
            "CASE WHEN 'id:asc' IN :sortCriteria THEN o.id END ASC, " +
            "CASE WHEN 'id:desc' IN :sortCriteria THEN o.id END DESC, " +
            "CASE WHEN 'date:asc' IN :sortCriteria THEN o.createdDate END ASC, " +
            "CASE WHEN 'date:desc' IN :sortCriteria THEN o.createdDate END DESC, " +
            "CASE WHEN 'status:asc' IN :sortCriteria THEN o.status END ASC, " +
            "CASE WHEN 'status:desc' IN :sortCriteria THEN o.status END DESC, " +
            "CASE WHEN 'total:asc' IN :sortCriteria THEN o.total END ASC, "+
            "CASE WHEN 'total:desc' IN :sortCriteria THEN o.total END DESC " )
    Page<Order> filter(
            @Param("status") EOrderStatus status,
            @Param("key") String key,
            @Param("sortCriteria") List<String> sortCriteria,
            Pageable pageable);

    Long countByStatus(EOrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND DATE(o.createdDate) = :date")
    Long countByDate(
            @Param("date") LocalDate date,
            @Param("status") EOrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND MONTH(o.createdDate) = :month AND YEAR(o.createdDate) = :year")
    Long countByMonthAndYear(
            @Param("month") int month,
            @Param("year") int year,
            @Param("status") EOrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND YEAR(o.createdDate) = :year")
    Long countByYear(
            @Param("year") int year,
            @Param("status") EOrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE YEAR(o.createdDate) = :year AND MONTH(o.createdDate) = :month AND o.status = 'DELIVERED'")
    Long getMonthlyOrderComplete(@Param("month") int month, @Param("year") int year);

    Long countByStatusAndUser_Username(EOrderStatus status, String username);

    @Query("SELECT SUM(o.total) " +
            "FROM Order o " +
            "WHERE o.status = 'DELIVERED' " +
            "AND YEAR(o.createdDate) = :year " +
            "AND MONTH(o.createdDate) = :month")
    BigDecimal getMonthlyTotalRevenue(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(o.total) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal getCountRevenue();

    @Query("SELECT COUNT(oi) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "where o.status = 'DELIVERED'")
    Long countAllSold();

    @Query("SELECT DISTINCT o FROM Order o JOIN o.user u JOIN o.orderItems oi " +
            "WHERE o.isDeleted = false " +
            "AND (LOWER(u.username) = :username)" +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND ((:key IS NOT NULL AND (LOWER(oi.product.name) LIKE %:key%) " +
            "OR (:key IS NULL))) " +
            "ORDER BY o.createdDate DESC ")
    Page<Order> userFilter(
            @Param("status") EOrderStatus status,
            @Param("key") String key,
            @Param("username") String username,
            Pageable pageable);

    Optional<Order> findByOrderCode(String orderCode);
}
