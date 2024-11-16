package com.example.brokeragefirmapp.mapper;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import com.example.brokeragefirmapp.dto.AssetDTO;
import com.example.brokeragefirmapp.entity.Asset;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssetMapper extends BaseMapper<Asset, AssetDTO> {

}
