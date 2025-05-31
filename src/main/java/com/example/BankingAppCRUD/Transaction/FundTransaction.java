package com.example.BankingAppCRUD.Transaction;

import com.example.BankingAppCRUD.Account.Model.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
public class FundTransaction {


    private Account sender;
    private Account receiver;
    private Date timeStamp;

    private Double amount;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;


}
