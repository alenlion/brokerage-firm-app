package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.AssetDTO;
import com.example.brokeragefirmapp.entity.Customer;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

public interface AdminService {
    void matchPendingOrders();

    AssetDTO createAsset( AssetDTO assetDTO );

    void generateRandomAssets( Customer customer);
}
