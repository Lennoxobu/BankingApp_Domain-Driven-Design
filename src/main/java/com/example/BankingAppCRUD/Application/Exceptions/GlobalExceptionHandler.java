package com.example.BankingAppCRUD.Application.Exceptions;

import com.example.BankingAppCRUD.Application.DTOs.OperationalResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.security.auth.login.AccountNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<OperationalResultDTO> handleAccountNotFoundException(
            AccountNotFoundException ex, WebRequest request) {

        logger.warn("Account not found: {}", ex.getMessage());

        OperationalResultDTO error = new OperationalResultDTO();
        error.setSuccess(false);
        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<OperationalResultDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        logger.warn("Invalid argument: {}", ex.getMessage());

        OperationalResultDTO error = new OperationalResultDTO();
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
    public ResponseEntity<OperationalResultDTO> handleGeneralException(
            Exception ex, WebRequest request) {

        logger.error("Unexpected error: ", ex.getMessage());

        OperationalResultDTO error = new OperationalResultDTO();
        error.setSuccess(false);
        error.setMessage("An unexpected error occurred");

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(AccountActionFailedException.class)
    public ResponseEntity<OperationalResultDTO> handleAccountActionFailedException (
            AccountActionFailedException ex , WebRequest request
    ) {

        logger.error("Account action failed", ex.getMessage());
        OperationalResultDTO error = new OperationalResultDTO();
        error.setSuccess(false);
        error.setMessage("Account failed please check action and try again");

        return new ResponseEntity<>(error , HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(InsufficientAmountException.class)
    public ResponseEntity<OperationalResultDTO> handleInsufficientAmountException (
            InsufficientAmountException ex , WebRequest request
    ) {
        logger.error("Insufficient funds please check your entry", ex.getMessage());
        OperationalResultDTO error = new OperationalResultDTO();
        error.setSuccess(false);
        error.setMessage("Insufficient funds please check your entry");

        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(UserAccountNotActiveException.class)
    public ResponseEntity<OperationalResultDTO> handleUserNotActiveException (
            UserAccountNotActiveException ex , WebRequest request
    ) {

        logger.error("User account not active", ex.getMessage());

        OperationalResultDTO error = new OperationalResultDTO();
        error.setSuccess(false);
        error.setMessage("User account not active");

        return new ResponseEntity<>(error , HttpStatus.UNPROCESSABLE_ENTITY);
    }



    @ExceptionHandler (UserActionFailedException.class)
    public ResponseEntity<OperationalResultDTO> handleUserActionFailedException (
            OperationalResultDTO ex , WebRequest request
    ) {

        logger.error("User action failed please try at another time" , ex.getMessage() );

        OperationalResultDTO error = new OperationalResultDTO();
        error.setSuccess(false);
        error.setMessage(ex.getMessage());


        return  new ResponseEntity<> (error ,HttpStatus.UNPROCESSABLE_ENTITY);

    }


    @ExceptionHandler(UserAccountNotFoundException.class)
    public ResponseEntity<OperationalResultDTO> handleUserAccountNotFoundException(
            UserAccountNotFoundException ex, WebRequest request) {

        logger.warn(" User Account not found: {}", ex.getMessage());

        OperationalResultDTO error = new OperationalResultDTO();
        error.setSuccess(false);
        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }










}
