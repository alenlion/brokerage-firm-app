package com.example.brokeragefirmapp.entity;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "assets", indexes = {
        @Index(name = "idx_customer_id", columnList = "customerId"),
        @Index(name = "idx_asset_name", columnList = "assetName")
})
@Getter
@Setter
public class Asset extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private String assetName;

    private BigDecimal size;
    private BigDecimal usableSize;

    @Version
    private Long version;

}

