package com.hdshop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;

    private final String fieldName;

    private Long fieldValue;

    private String StringValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, String StringValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, StringValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.StringValue = StringValue;
    }
}
