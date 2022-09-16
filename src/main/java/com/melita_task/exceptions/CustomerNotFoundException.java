package com.melita_task.exceptions;

public class CustomerNotFoundException extends LogicalErrorException {

    public CustomerNotFoundException(final int id) {
        super("CUSTOMER_NOT_FOUND_"+id);
    }

}
