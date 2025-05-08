package com.example.BankingAppCRUD.Account.Model;

import com.example.BankingAppCRUD.User.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Table(name= "_CheckingAccount")
@ToString
@Entity
public class CheckingAccount extends Account {

    private int debitCardNo;
    private int debitCardPin;

    @OneToOne
    @JoinColumn(name ="UserId" , nullable = false)
    private User user;


   @Builder
    CheckingAccount (String accountNumber ,String NI , double balance , Long Id, double rate , int debitCardNo, int debitCardPin ) {
        super( accountNumber, NI, rate ,balance ,  Id );

        this.debitCardNo = debitCardNo;
        this.debitCardPin = debitCardPin;

    }



}
