package com.example.BankingAppCRUD.Account.Model;


import com.example.BankingAppCRUD.User.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Table(name="_SavingAccount")
@ToString
@Entity
public class SavingAccount extends Account {

    private int safetyID;
    private int safetyKey;

    @OneToOne
    @JoinColumn(name ="UserId" , nullable = false)
    private User user;

    @Builder
    SavingAccount(String accountNumber ,String NI ,  double balance ,Long Id, double rate, int safetyID, int safetyKey ) {
        super( accountNumber , NI , rate ,balance , Id);
        this.safetyID = safetyID;
        this.safetyKey = safetyKey;

    }








}
