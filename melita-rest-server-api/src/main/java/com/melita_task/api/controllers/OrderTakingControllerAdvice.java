package com.melita_task.api.controllers;

import com.melita_task.api.exceptions.LogicalError;
import com.melita_task.api.exceptions.LogicalErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@Slf4j
@ControllerAdvice()
public class OrderTakingControllerAdvice {

    @ExceptionHandler(LogicalErrorException.class)
    public ResponseEntity<LogicalError> handleLogicalErrorException(final LogicalErrorException ex) {

        return ResponseEntity.unprocessableEntity()
                .body(new LogicalError(ex.getCode()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<LogicalError> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<LogicalError> handleException(final Throwable ex) {

        return ResponseEntity.internalServerError()
                .body(new LogicalError(ex.getMessage()));
    }

}
