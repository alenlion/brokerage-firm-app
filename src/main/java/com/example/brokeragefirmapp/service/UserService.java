package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.CustomerDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

@Service
public class UserService {
    private final CustomerService customerService;

    public UserService( CustomerService customerService ) {
        this.customerService = customerService;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null && authentication.isAuthenticated() ) {
            UserDetails userDetails = ( UserDetails ) authentication.getPrincipal();
            CustomerDTO customer = customerService.getCustomerByUsername( userDetails.getUsername() );
            return customer.getId();
        }
        return null;
    }
}
