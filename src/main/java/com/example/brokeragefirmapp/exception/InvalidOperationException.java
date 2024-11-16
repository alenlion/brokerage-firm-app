package com.example.brokeragefirmapp.exception;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException( String message ) {
        super( message );
    }
}