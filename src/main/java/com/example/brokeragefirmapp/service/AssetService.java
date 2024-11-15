package com.example.brokeragefirmapp.service;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import com.example.brokeragefirmapp.dto.AssetDTO;

import java.math.BigDecimal;
import java.util.List;

public interface AssetService {
    List<AssetDTO> listAssets( Long customerId);
    void depositMoney(Long customerId, BigDecimal amount);
    void withdrawMoney(Long customerId, BigDecimal amount, String iban);
}
