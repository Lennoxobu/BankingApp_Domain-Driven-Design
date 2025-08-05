package com.example.BankingAppCRUD.Application.Exceptions;

public class UserAccountNotActive extends RuntimeException {
  public UserAccountNotActive(String message) {
    super(message);
  }
}
