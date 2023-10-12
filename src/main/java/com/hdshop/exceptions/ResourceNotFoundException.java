package com.hdshop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Long getFieldValue() {
        return fieldValue;
    }

    public String getStringValue() {
        return StringValue;
    }
}
