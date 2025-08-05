package com.example.BankingAppCRUD.Application.Exceptions;

public class InsufficientAmountException extends RuntimeException {
    public InsufficientAmountException(String message) {
        super(message);
    }
}
