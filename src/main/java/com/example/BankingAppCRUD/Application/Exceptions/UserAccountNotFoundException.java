package com.example.BankingAppCRUD.Application.Exceptions;

public class UserAccountNotFound extends RuntimeException {
  public UserAccountNotFound(String message) {
    super(message);
  }
}
