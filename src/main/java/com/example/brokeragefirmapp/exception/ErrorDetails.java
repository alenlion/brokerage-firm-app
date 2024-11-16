package com.example.brokeragefirmapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

@Getter
@Setter
@AllArgsConstructor
public class ErrorDetails {
    private LocalDateTime timestamp;
    private int statusCode;
    private String error;
    private String message;
    private String path;
}
