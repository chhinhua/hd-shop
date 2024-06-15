package com.duck.repository;

import com.duck.entity.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
}
