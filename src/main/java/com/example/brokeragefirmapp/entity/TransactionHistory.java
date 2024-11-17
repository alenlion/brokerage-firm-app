package com.example.brokeragefirmapp.entity;

import com.example.brokeragefirmapp.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class TransactionHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    @Enumerated( EnumType.STRING)
    private TransactionType transactionType; // DEPOSIT, WITHDRAWAL

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    private String iban; // Applicable for withdrawals

}
