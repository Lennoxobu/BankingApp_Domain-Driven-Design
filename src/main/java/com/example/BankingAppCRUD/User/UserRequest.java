package com.example.BankingAppCRUD.User;

import com.example.BankingAppCRUD.Account.CheckingAccount;
import com.example.BankingAppCRUD.Account.SavingAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRequest {

    private String  firstName;
    private String lastName;

    private CheckingAccount checkingAccount;
    private SavingAccount savingAccount;




}
