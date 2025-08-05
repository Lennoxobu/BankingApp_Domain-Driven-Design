package com.example.BankingAppCRUD.Application.Exceptions;

public class AccountActionFailedException extends RuntimeException {
    public AccountActionFailedException(String message) {

        super(message);
    }
}
