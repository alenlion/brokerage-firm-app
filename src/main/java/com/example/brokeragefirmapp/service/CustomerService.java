package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.CustomerDTO;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

public interface CustomerService {
    CustomerDTO createCustomer( CustomerDTO customerDTO);
    CustomerDTO getCustomerByUsername(String username);
}
