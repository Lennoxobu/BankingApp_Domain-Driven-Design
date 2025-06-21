package com.example.BankingAppCRUD.Application.Request;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
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
