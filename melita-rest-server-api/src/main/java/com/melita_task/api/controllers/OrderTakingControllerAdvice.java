package com.melita_task.api.controllers;

import com.melita_task.api.exceptions.LogicalError;
import com.melita_task.api.exceptions.LogicalErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice()
public class OrderTakingControllerAdvice {

    @ExceptionHandler(LogicalErrorException.class)
    public ResponseEntity<LogicalError> handleLogicalErrorException(final LogicalErrorException ex) {

        return ResponseEntity.unprocessableEntity()
                .body(new LogicalError(ex.getCode()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<LogicalError> handleException(final Throwable ex) {

        return ResponseEntity.internalServerError()
                .body(new LogicalError(ex.getMessage()));
    }

}

