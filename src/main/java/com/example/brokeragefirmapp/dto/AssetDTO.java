package com.example.brokeragefirmapp.dto;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class AssetDTO extends BaseDTO {
    private Long customerId;
    private String assetName;
    private BigDecimal size;
    private BigDecimal usableSize;
}
