package com.example.BankingAppCRUD.User;

import com.example.BankingAppCRUD.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Account.Model.SavingAccount;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@Table(name="_User")
@Builder
public class User {

    private String firstName;

    private String lastName;

    @OneToOne()
    @JoinColumn(name ="checkingAccountId")
    private CheckingAccount checkingAccount;

    @OneToOne()
    @JoinColumn(name="savingAccountId")
    private SavingAccount savingAccount;

    @GeneratedValue
    @Id 
    private Long Id;










}
