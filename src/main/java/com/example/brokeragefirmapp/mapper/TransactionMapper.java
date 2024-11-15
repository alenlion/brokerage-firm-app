package com.example.brokeragefirmapp.mapper;

import com.example.brokeragefirmapp.entity.TransactionHistory;
import com.example.brokeragefirmapp.dto.TransactionHistoryDTO;
import org.mapstruct.Mapper;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */


@Mapper(componentModel = "spring")
public interface TransactionMapper extends BaseMapper<TransactionHistory, TransactionHistoryDTO> {
}

