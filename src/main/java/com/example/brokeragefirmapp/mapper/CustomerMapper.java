package com.example.brokeragefirmapp.mapper;

import com.example.brokeragefirmapp.dto.CustomerDTO;
import com.example.brokeragefirmapp.entity.Customer;
import org.mapstruct.Mapper;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

@Mapper(componentModel = "spring")
public interface CustomerMapper extends BaseMapper<Customer, CustomerDTO> {
}
