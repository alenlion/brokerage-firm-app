package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.TransactionHistoryDTO;

import java.util.List;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

public interface TransactionService {
    void recordTransaction( TransactionHistoryDTO transaction);
    List<TransactionHistoryDTO> getTransactions( Long customerId);
}
