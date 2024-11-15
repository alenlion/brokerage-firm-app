package com.example.brokeragefirmapp.controller;

import com.example.brokeragefirmapp.dto.ApiResponse;
import com.example.brokeragefirmapp.service.AssetService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final AssetService assetService;

    public CustomerController( AssetService assetService ) {
        this.assetService = assetService;
    }

    @PostMapping("/{customerId}/deposit")
    @PreAuthorize("hasRole('ADMIN') or #customerId.toString() == principal")
    public ResponseEntity<ApiResponse<Void>> depositMoney( @PathVariable Long customerId,
                                                           @RequestParam @DecimalMin("0.01") BigDecimal amount) {
        assetService.depositMoney(customerId, amount);
        ApiResponse<Void> response = new ApiResponse<>(true, "Money deposited successfully", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{customerId}/withdraw")
    @PreAuthorize("hasRole('ADMIN') or #customerId.toString() == principal")
    public ResponseEntity<ApiResponse<Void>> withdrawMoney(@PathVariable Long customerId,
                                                           @RequestParam @DecimalMin("0.01") BigDecimal amount,
                                                           @RequestParam @NotBlank String iban) {
        assetService.withdrawMoney(customerId, amount, iban);
        ApiResponse<Void> response = new ApiResponse<>(true, "Money withdrawn successfully", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
