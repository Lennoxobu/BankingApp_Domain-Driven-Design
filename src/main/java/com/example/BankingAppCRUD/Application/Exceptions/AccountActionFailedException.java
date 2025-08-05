package com.example.BankingAppCRUD.Application.Exceptions;

public class AccountActionNotFailed extends RuntimeException {
    public AccountActionNotFailed(String message) {

        super(message);
    }
}
