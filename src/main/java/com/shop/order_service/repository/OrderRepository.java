package com.shop.order_service.repository;

import com.shop.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Talks to the database for Order entities.
 * Extending JpaRepository gives us save(), findById(), findAll(), etc. for free.
 * <Order, Long> = manages Order entities whose id (orderId) is a Long.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // basic CRUD is provided automatically; no code needed yet
}
