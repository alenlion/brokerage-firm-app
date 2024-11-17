package com.example.brokeragefirmapp.entity;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
public class Asset extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    @NotBlank(message = "assetName is required")
    private String assetName;

    @Column(precision = 10, scale = 2)
    private BigDecimal size;

    @Column(precision = 10, scale = 2)
    private BigDecimal usableSize;

    public Asset( Long customerId, String assetName, BigDecimal size, BigDecimal usableSize ) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.size = size;
        this.usableSize = usableSize;
    }

}

