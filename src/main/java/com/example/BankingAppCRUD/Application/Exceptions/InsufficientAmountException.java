package com.example.BankingAppCRUD.Application.Exceptions;

public class InsuffcientAmount extends RuntimeException {
    public InsuffcientAmount(String message) {
        super(message);
    }
}
