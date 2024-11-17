package com.example.brokeragefirmapp.dto;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseDTO {
    private Long id;

    private String createUser;

    private String modifyUser;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

   // private Long version;

}
