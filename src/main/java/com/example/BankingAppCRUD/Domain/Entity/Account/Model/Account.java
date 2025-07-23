package com.example.BankingAppCRUD.Domain.Entity.Account.Model;

import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import com.example.BankingAppCRUD.Domain.ValueObject.AccountInfo;
import com.example.BankingAppCRUD.Domain.ValueObject.AccountStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@MappedSuperclass
@AllArgsConstructor
@SuperBuilder
public abstract class Account {

    protected AccountStatus account_status;
    protected Timestamp createdAt;

    @Embedded
    protected AccountInfo info;


    @Embedded
    protected Money  balance;

    protected List<UUID> account_transactions;

    @Embedded
    protected Rate rate;


    @Id
    protected UUID id;



}
