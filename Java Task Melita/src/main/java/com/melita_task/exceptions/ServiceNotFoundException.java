package com.melita_task.exceptions;

public class ServiceNotFoundException extends Exception {
    public ServiceNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
