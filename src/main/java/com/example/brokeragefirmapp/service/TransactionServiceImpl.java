package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.TransactionHistoryDTO;
import com.example.brokeragefirmapp.entity.TransactionHistory;
import com.example.brokeragefirmapp.mapper.TransactionMapper;
import com.example.brokeragefirmapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */


@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl( TransactionRepository transactionRepository, TransactionMapper transactionMapper ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public void recordTransaction( TransactionHistoryDTO transaction) {
        TransactionHistory transactionEntity = transactionMapper.toEntity( transaction );
        transactionRepository.save(transactionEntity);
    }

    @Override
    public List<TransactionHistoryDTO> getTransactions( Long customerId) {
        return transactionRepository.findByCustomerId(customerId)
                .stream()
                .map(transactionMapper::toDTO)
                .collect( Collectors.toList());
    }
}
