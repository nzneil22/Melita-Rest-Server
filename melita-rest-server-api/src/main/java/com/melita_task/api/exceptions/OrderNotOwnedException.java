package com.melita_task.api.exceptions;

public class OrderNotOwnedException extends LogicalErrorException {

    public OrderNotOwnedException() { super("ORDER_DOES_NOT_BELONG_TO_CUSTOMER"); }

}
