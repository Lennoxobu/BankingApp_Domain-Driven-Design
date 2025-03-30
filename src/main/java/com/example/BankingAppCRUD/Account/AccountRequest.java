package com.example.BankingAppCRUD.Account;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
public class AccountRequest {

    @NonNull
    private String firstName;
    @NonNull
    private String lastName;

    private double balance;

    @NonNull
    private String accountNumber;
    @NonNull
    private String NI;

    private double rate;

    private int debitCardNo;
    private int debitCardPin;
    private int safetyID;
    private int safetyKey;




}
