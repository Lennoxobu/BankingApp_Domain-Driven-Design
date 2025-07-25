package com.example.BankingAppCRUD.Application.Exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.security.auth.login.AccountNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<OperationResultDto> handleAccountNotFoundException(
            AccountNotFoundException ex, WebRequest request) {

        logger.warn("Account not found: {}", ex.getMessage());

        OperationResultDto error = new OperationResultDto();
        error.setSuccess(false);
        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<OperationResultDto> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        logger.warn("Invalid argument: {}", ex.getMessage());

        OperationResultDto error = new OperationResultDto();
        error.setSuccess(false);
        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.warn("Validation errors: {}", errors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OperationResultDto> handleGeneralException(
            Exception ex, WebRequest request) {

        logger.error("Unexpected error: ", ex);

        OperationResultDto error = new OperationResultDto();
        error.setSuccess(false);
        error.setMessage("An unexpected error occurred");

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
