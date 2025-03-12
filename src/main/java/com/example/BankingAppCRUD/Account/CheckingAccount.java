package com.example.BankingAppCRUD.Account;

import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Table(name= "_CheckingAccount")
@ToString
public class CheckingAccount extends Account {

    private int debitCardNo;
    private int debitCardPin;



   @Builder
    CheckingAccount (String firstName , String lastName ,String accountNumber ,String NI , double balance , Long Id, double rate , int debitCardNo, int debitCardPin ) {
        super(firstName , lastName , accountNumber, NI, rate ,balance ,  Id );

        this.debitCardNo = debitCardNo;
        this.debitCardPin = debitCardPin;

    }



}
