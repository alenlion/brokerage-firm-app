package com.example.brokeragefirmapp.dto;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AssetDTO extends BaseDTO {
    private Long customerId;

    private String assetName;

    private BigDecimal size;

    private BigDecimal usableSize;

    public AssetDTO( Long customerId, String assetName, BigDecimal size, BigDecimal usableSize ) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.size = size;
        this.usableSize = usableSize;
    }

}
