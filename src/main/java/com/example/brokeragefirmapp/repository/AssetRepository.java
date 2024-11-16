package com.example.brokeragefirmapp.repository;

import com.example.brokeragefirmapp.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByCustomerId( Long customerId);
    Optional<Asset> findByCustomerIdAndAssetName( Long customerId, String assetName);
}

