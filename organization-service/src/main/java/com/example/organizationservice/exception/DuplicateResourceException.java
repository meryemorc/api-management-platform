package com.example.organizationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException { //üretilen seyin tekrar üretilmeye calısmasında verilir
    public DuplicateResourceException(String message) {
        super(message);
    }
}