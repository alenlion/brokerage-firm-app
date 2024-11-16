package com.example.brokeragefirmapp.controller;

import com.example.brokeragefirmapp.dto.ApiResponse;
import com.example.brokeragefirmapp.dto.CustomerDTO;
import com.example.brokeragefirmapp.security.CustomerDetailsService;
import com.example.brokeragefirmapp.security.JwtUtil;
import com.example.brokeragefirmapp.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final CustomerService customerService;

    private final JwtUtil jwtUtil;
    private final CustomerDetailsService customerDetailsService;

    public AuthController( AuthenticationManager authenticationManager, CustomerService customerService, JwtUtil jwtUtil, CustomerDetailsService customerDetailsService ) {
        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
        this.customerDetailsService = customerDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerDTO>> register( @Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        ApiResponse<CustomerDTO> response = new ApiResponse<>(true, "User registered successfully", createdCustomer);
        return ResponseEntity.status( HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login( @Valid @RequestBody CustomerDTO customerDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(customerDTO.getUsername(), customerDTO.getPassword())
        );

        String username = customerDTO.getUsername();
        UserDetails userDetails = customerDetailsService.loadUserByUsername(username);
        String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities().toString());
        ApiResponse<String> response = new ApiResponse<>(true, "Login successful", token);
        return ResponseEntity.ok(response);
    }
}
