package com.example.BankingAppCRUD.Account;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(name="_SavingAccount")
@ToString
public class SavingAccount extends Account {

    private int safetyID;
    private int safetyKey;

    SavingAccount(String firstName , String lastName ,String accountNumber ,String NI ,  double balance ,Long Id, double rate, int safetyID, int safetyKey ) {
        super(firstName , lastName , accountNumber , NI , rate ,balance , Id);

        this.safetyID = safetyID;
        this.safetyKey = safetyKey;

    }








}
