package com.example.melita;

class ServiceNotFoundException extends Exception {
    public ServiceNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
