package com.example.BankingAppCRUD.Application.Exceptions;

public class UserActionFailedException extends RuntimeException {
    public UserActionFailedException(String message) {
        super(message);
    }
}
