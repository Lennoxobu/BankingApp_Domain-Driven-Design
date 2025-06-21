package com.example.BankingAppCRUD.Domain.Entity.Account.Model;

import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import com.example.BankingAppCRUD.Domain.ValueObject.AccountInfo;
import com.example.BankingAppCRUD.Domain.ValueObject.AccountStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import jakarta.persistence.*;
import lombok.*;


import java.security.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@MappedSuperclass
@AllArgsConstructor
public abstract class Account {

    protected AccountStatus account_status;
    protected Timestamp createdAt;

    @Embedded
    protected AccountInfo info;



    protected List<FundTransaction> account_transactions;

    @Embedded
    protected Rate rate;


    @Id
    protected UUID id;



}
