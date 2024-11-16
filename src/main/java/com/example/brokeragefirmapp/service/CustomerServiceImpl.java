package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.CustomerDTO;
import com.example.brokeragefirmapp.entity.Customer;
import com.example.brokeragefirmapp.exception.ResourceNotFoundException;
import com.example.brokeragefirmapp.mapper.CustomerMapper;
import com.example.brokeragefirmapp.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final PasswordEncoder passwordEncoder;
    
    public CustomerServiceImpl( CustomerRepository customerRepository, CustomerMapper customerMapper, PasswordEncoder passwordEncoder ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerDTO createCustomer( CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setRole("USER");
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(savedCustomer);
    }

    @Override
    public CustomerDTO getCustomerByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return customerMapper.toDTO(customer);
    }
}
