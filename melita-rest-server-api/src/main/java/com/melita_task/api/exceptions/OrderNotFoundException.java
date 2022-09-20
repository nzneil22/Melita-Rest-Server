package com.melita_task.api.exceptions;

public class OrderNotFoundException extends LogicalErrorException {

    public OrderNotFoundException() {
        super("ORDER_NOT_FOUND");
    }

}
