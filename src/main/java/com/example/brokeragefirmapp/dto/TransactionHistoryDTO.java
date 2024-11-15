package com.example.brokeragefirmapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */


@Getter
@Setter
public class TransactionHistoryDTO extends BaseDTO {
    private Long id;
    private Long customerId;
    private String transactionType;
    private BigDecimal amount;
    private String iban;
}
