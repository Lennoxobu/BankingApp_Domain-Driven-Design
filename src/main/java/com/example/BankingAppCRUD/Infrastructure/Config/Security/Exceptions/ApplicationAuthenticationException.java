package com.example.BankingAppCRUD.Infrastructure.Config.Security.Exceptions;

import javax.naming.AuthenticationException;

public class ApplicationAuthenticationException extends AuthenticationException {
    public ApplicationAuthenticationException(String message) {
        super(message);
    }
}
