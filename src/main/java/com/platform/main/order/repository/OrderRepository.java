package com.platform.main.order.repository;

import com.platform.main.order.entity.Order;
import com.platform.main.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUser_Id(UUID userId, Pageable pageable);
    Page<Order> findByUser_IdAndStatus(UUID userId, OrderStatus status, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    int countByCreatedAtAfter(LocalDateTime since);
}