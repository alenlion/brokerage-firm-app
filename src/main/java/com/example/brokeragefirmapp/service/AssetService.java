package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.AssetDTO;
import com.example.brokeragefirmapp.dto.DepositRequestDTO;
import com.example.brokeragefirmapp.dto.WithdrawRequestDTO;

import java.util.List;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

public interface AssetService {
    List<AssetDTO> listAssets( Long customerId );

    void depositMoney( DepositRequestDTO depositRequest );

    void withdrawMoney( WithdrawRequestDTO withdrawRequest );
}
