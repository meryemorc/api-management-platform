package com.example.organizationservice.exception;

public class GlobalExceptionHandler extends RuntimeException { //diger ücünü ve onun dısındaki tum hataları controllerla yakalar
    public GlobalExceptionHandler(String message) {
        super(message);
    }
}
