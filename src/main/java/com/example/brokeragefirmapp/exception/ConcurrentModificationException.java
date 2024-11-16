package com.example.brokeragefirmapp.exception;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

public class ConcurrentModificationException extends RuntimeException {
    public ConcurrentModificationException( String message ) {
        super( message );
    }
}