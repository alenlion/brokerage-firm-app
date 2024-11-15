package com.example.brokeragefirmapp.repository;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import com.example.brokeragefirmapp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
}

