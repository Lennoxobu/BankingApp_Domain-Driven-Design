package com.example.BankingAppCRUD.Application.Exceptions;

public class AccountNotActive extends RuntimeException {
    public AccountNotActive(String message) {
        super(message);
    }
}
