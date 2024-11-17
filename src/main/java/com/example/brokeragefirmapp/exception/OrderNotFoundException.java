package com.example.brokeragefirmapp.exception;

/**
 * @author Rayan Aksu
 * @since 11/17/2024
 */

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException( String message ) {
        super( message );
    }
}