package com.duck.repository;

import com.duck.entity.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
    @Query("SELECT ot FROM OrderTracking ot WHERE ot.order.id = :order_id")
    List<OrderTracking> findAllByOrderId(@Param("order_id") Long orderId);
}
