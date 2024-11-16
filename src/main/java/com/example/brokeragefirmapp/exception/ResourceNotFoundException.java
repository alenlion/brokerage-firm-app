package com.example.brokeragefirmapp.exception;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException( String message ) {
        super( message );
    }
}
