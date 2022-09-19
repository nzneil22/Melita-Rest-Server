package com.melita_task.api.exceptions;

public class ClientNotFoundException extends LogicalErrorException {

    public ClientNotFoundException(final String id) {
        super("CLIENT_NOT_FOUND_"+id);
    }

}
