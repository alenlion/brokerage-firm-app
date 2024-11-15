package com.example.brokeragefirmapp.dto;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}

