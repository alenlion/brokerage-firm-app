package com.example.brokeragefirmapp.dto;

import com.example.brokeragefirmapp.enums.OrderSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author Rayan Aksu
 * @since 11/17/2024
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Asset name is required")
    private String assetName;

    @NotNull(message = "Order side is required")
    private OrderSide orderSide;

    @NotNull(message = "Size is required")
    @Positive(message = "Size must be positive")
    private BigDecimal size;

    @NotNull(message = "Size is required")
    @Positive(message = "Size must be positive")
    private BigDecimal orderSize;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
}
