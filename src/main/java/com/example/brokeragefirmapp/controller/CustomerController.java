package com.example.brokeragefirmapp.controller;

import com.example.brokeragefirmapp.dto.ApiResponse;
import com.example.brokeragefirmapp.dto.DepositRequestDTO;
import com.example.brokeragefirmapp.dto.TransactionHistoryDTO;
import com.example.brokeragefirmapp.dto.WithdrawRequestDTO;
import com.example.brokeragefirmapp.service.AssetService;
import com.example.brokeragefirmapp.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

@RestController
@RequestMapping( "/api/customers" )
public class CustomerController {

    private final AssetService assetService;

    private final TransactionService transactionService;

    public CustomerController( AssetService assetService, TransactionService transactionService ) {
        this.assetService = assetService;
        this.transactionService = transactionService;
    }

    @PostMapping( "/deposit" )
    @PreAuthorize( "hasRole('ADMIN') or @userService.getCurrentUserId() == #depositRequestDTO.customerId" )
    public ResponseEntity<ApiResponse<Void>> depositMoney( @Valid @RequestBody DepositRequestDTO depositRequest ) {
        assetService.depositMoney( depositRequest );
        ApiResponse<Void> response = new ApiResponse<>( true, "Money deposited successfully", null );
        return ResponseEntity.status( HttpStatus.OK ).body( response );
    }

    @PostMapping( "/withdraw" )
    @PreAuthorize( "hasRole('ADMIN') or @userService.getCurrentUserId() == #withdrawRequestDTO.customerId" )
    public ResponseEntity<ApiResponse<Void>> withdrawMoney( @Valid @RequestBody WithdrawRequestDTO withdrawRequestDTO ) {
        assetService.withdrawMoney( withdrawRequestDTO );
        ApiResponse<Void> response = new ApiResponse<>( true, "Money withdrawn successfully", null );
        return ResponseEntity.status( HttpStatus.OK ).body( response );
    }

    @GetMapping( "/{customerId}/transactions" )
    @PreAuthorize( "hasRole('ADMIN') or @userService.getCurrentUserId() == #customerId" )
    public ResponseEntity<ApiResponse<List<TransactionHistoryDTO>>> getTransactions( @PathVariable Long customerId ) {
        List<TransactionHistoryDTO> transactions = transactionService.getTransactions( customerId );
        ApiResponse<List<TransactionHistoryDTO>> response = new ApiResponse<>( true, "Transactions retrieved successfully", transactions );
        return ResponseEntity.ok( response );
    }
}
