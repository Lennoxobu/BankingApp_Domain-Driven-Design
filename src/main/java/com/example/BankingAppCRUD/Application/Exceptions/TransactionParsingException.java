package com.example.BankingAppCRUD.Application.Exceptions;

public class TransactionParsingException extends RuntimeException {


    public TransactionParsingException (String message) {
        super(message);
    }

}
