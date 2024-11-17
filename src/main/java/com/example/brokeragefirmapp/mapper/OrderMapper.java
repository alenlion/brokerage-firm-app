package com.example.brokeragefirmapp.mapper;

import com.example.brokeragefirmapp.dto.OrderCreateRequest;
import com.example.brokeragefirmapp.dto.OrderDTO;
import com.example.brokeragefirmapp.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

@Mapper( componentModel = "spring" )
public interface OrderMapper extends BaseMapper<Order, OrderDTO> {

    @Override
    @Mapping( target = "orderSide", source = "orderSide" )
    @Mapping( target = "status", source = "status" )
    OrderDTO toDTO( Order entity );

    @Override
    @Mapping( target = "orderSide", source = "orderSide" )
    @Mapping( target = "status", source = "status" )
    Order toEntity( OrderDTO dto );

    @Mapping( target = "id", ignore = true )
    @Mapping( target = "status", ignore = true )
    @Mapping( target = "createDate", ignore = true )
    Order toEntity( OrderCreateRequest request );
}
