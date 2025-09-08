package com.example.BankingAppCRUD.Application.Exceptions;

public class TransactionSearchResultNotFoundException extends RuntimeException {
    public TransactionSearchResultNotFoundException(String message) {
        super(message);
    }
}
