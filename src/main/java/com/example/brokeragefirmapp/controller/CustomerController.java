package com.example.brokeragefirmapp.controller;

import com.example.brokeragefirmapp.dto.ApiResponse;
import com.example.brokeragefirmapp.dto.DepositRequestDTO;
import com.example.brokeragefirmapp.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

@RestController
@RequestMapping( "/api/customers" )
public class CustomerController {

    private final AssetService assetService;

    public CustomerController( AssetService assetService ) {
        this.assetService = assetService;
    }

    @PostMapping( "/deposit" )
    public ResponseEntity<ApiResponse<Void>> depositMoney( @Valid @RequestBody DepositRequestDTO depositRequestDTO ) {
        assetService.depositMoney( depositRequestDTO.getCustomerId(), depositRequestDTO.getAmount() );
        ApiResponse<Void> response = new ApiResponse<>( true, "Money deposited successfully", null );
        return ResponseEntity.status( HttpStatus.OK ).body( response );
    }
}
