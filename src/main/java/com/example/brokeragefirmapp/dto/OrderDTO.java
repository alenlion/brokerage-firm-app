package com.example.brokeragefirmapp.dto;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderDTO extends BaseDTO {

    private Long id;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Asset name is required")
    private String assetName;

    @NotBlank(message = "Order side is required")
    @Pattern(regexp = "BUY|SELL", message = "Order side must be BUY or SELL")
    private String orderSide;

    @NotNull(message = "Size is required")
    @DecimalMin(value = "0.01", inclusive = false, message = "Size must be greater than zero")
    private BigDecimal size;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    private String status;
}
