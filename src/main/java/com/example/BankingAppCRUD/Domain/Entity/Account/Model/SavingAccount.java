package com.example.BankingAppCRUD.Domain.Entity.Account.Model;


import com.example.BankingAppCRUD.Domain.ValueObject.Frequency;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Getter
@Setter
@Table(name="_SavingAccount")
@ToString
@SuperBuilder
@Entity
public class SavingAccount extends Account {

    @Embedded
    private Rate interestRate;
    @Embedded
    private Money interestAccrued;
    @Embedded
    private Money minBalance;

    private Frequency compoundFrequency;
    private Timestamp lastInterestedAppliedAt;




}
