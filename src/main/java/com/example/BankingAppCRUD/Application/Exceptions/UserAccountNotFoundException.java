package com.example.BankingAppCRUD.Application.Exceptions;

public class UserAccountNotFoundException extends RuntimeException {
    public UserAccountNotFoundException(String message) {
        super(message);
    }
}
