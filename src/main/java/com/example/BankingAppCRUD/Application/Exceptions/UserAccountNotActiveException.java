package com.example.BankingAppCRUD.Application.Exceptions;

public class UserAccountNotActiveException extends RuntimeException {
    public UserAccountNotActiveException(String message) {
        super(message);
    }
}
