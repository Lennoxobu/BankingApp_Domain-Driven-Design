package com.example.BankingAppCRUD.Application.Exceptions;

public class UserActionFailed extends RuntimeException {
    public UserActionFailed(String message) {
        super(message);
    }
}
