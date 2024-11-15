package com.example.brokeragefirmapp.mapper;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import org.mapstruct.MappingTarget;

public interface BaseMapper<E, D> {
    D toDTO(E entity);
    E toEntity(D dto);

    void updateEntityFromDTO(D dto, @MappingTarget E entity);
}
