package com.example.melita;

class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
