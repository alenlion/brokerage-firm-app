package com.example.brokeragefirmapp.exception;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException( String message ) {
        super( message );
    }
}