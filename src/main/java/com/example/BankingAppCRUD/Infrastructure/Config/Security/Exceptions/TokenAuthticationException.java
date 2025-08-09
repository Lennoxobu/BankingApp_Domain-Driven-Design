package com.example.BankingAppCRUD.Infrastructure.Config.Security.Exceptions;

public class TokenAuthticationException extends RuntimeException {
    public TokenAuthticationException(String message) {
        super(message);
    }
}
