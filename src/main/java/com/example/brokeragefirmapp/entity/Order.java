package com.example.brokeragefirmapp.entity;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */


import com.example.brokeragefirmapp.enums.OrderSide;
import com.example.brokeragefirmapp.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_customer_id", columnList = "customerId"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String assetName;

    @Enumerated( EnumType.STRING)
    private OrderSide orderSide; // BUY or SELL

    private BigDecimal size;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // PENDING, MATCHED, CANCELED
}
