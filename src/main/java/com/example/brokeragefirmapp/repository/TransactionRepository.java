package com.example.brokeragefirmapp.repository;

import com.example.brokeragefirmapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomerId( Long customerId);
}
