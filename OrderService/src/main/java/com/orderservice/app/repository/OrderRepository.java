package com.orderservice.app.repository;

import com.orderservice.app.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByCustomerId(int customerId);

    List<Order> findByOrderDateAfter(LocalDateTime time);

    Order findByPaymentSessionId(String sessionId);

    Order findByCartId(int i);
}
