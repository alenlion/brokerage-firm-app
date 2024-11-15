package com.example.brokeragefirmapp.mapper;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import com.example.brokeragefirmapp.dto.AssetDTO;
import com.example.brokeragefirmapp.entity.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssetMapper extends BaseMapper<Asset, AssetDTO> {
    @Override
    @Mapping(target = "customerId", source = "assetId.customerId")
    @Mapping(target = "assetName", source = "assetId.assetName")
    AssetDTO toDTO(Asset entity);
}
