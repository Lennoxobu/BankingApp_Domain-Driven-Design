package com.example.BankingAppCRUD.Infrastructure.Config.Security.Exceptions;

import javax.naming.AuthenticationException;

public class TokenAuthenticationException extends AuthenticationException {
    public TokenAuthenticationException(String message) {
        super(message);
    }
}
