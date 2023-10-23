package com.hdshop.dto;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
@Hidden
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;
}
