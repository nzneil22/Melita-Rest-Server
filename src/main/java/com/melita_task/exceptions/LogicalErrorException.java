package com.melita_task.exceptions;

import lombok.Data;

@Data
public class LogicalErrorException extends IllegalStateException {
    final String code;
}
