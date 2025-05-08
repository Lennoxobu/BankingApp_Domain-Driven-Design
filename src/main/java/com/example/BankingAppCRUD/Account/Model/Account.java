package com.example.BankingAppCRUD.Account.Model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@MappedSuperclass
@AllArgsConstructor
public  class Account implements baseRate {


    protected String accountNumber;
    protected String NI;
    protected double rate = baseRate.rate ;
    protected double balance;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;



}
