package com.example.BankingAppCRUD.Domain.Entity.Transaction.Model;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
public class FundTransaction {


    private UUID  senderID;
    private UUID  receiverID;
    private Date timeStamp;

    private Double amount;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID Id;


}
