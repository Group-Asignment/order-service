package com.shop.order_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The Order entity — one row per order.
 *
 * Note on the table name: the assignment calls the table "order", but ORDER is a
 * reserved keyword in SQL (used in "ORDER BY"). Naming the table literally "order"
 * causes database errors, so we map it to "orders" — the standard, safe convention.
 */
@Entity
@Table(name = "orders")          // "orders" avoids the SQL reserved word "order"
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // DB auto-generates the id
    private Long orderId;

    private Long customerId;      // who placed the order
    private Long productId;       // which product was ordered
    private String productName;   // copied from Product Service at order time
    private Integer quantity;     // how many units
    private Double totalPrice;    // unitPrice * quantity (calculated by us)

    private LocalDateTime orderDate;  // when the order was created
    private String status;            // e.g. "CREATED"
}
