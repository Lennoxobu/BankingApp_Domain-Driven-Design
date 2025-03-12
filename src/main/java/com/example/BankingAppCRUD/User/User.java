package com.example.BankingAppCRUD.User;

import com.example.BankingAppCRUD.Account.CheckingAccount;
import com.example.BankingAppCRUD.Account.SavingAccount;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name="_User")
@Builder
public class User {


    @NonNull
    private String firstName;
    @NonNull
    private String lastName;

    @OneToOne()
    private CheckingAccount checkingAccount;
    @OneToOne()
    private SavingAccount savingAccount;

    @GeneratedValue
    @Id 
    private Long Id;






}
