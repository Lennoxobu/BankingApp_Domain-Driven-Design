package com.example.BankingAppCRUD.Domain.Entity.Account.Model;


import com.example.BankingAppCRUD.Domain.ValueObject.Frequency;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;

@Getter
@Setter
@Table(name="_SavingAccount")
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause= "deleted = false" )
@Entity
public class SavingAccount extends Account {

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "country" , column = @Column(name = "interest_rate_country")),
            @AttributeOverride(name = "rateInfo" , column = @Column(name = "interest_rate_rateInfo")),
            @AttributeOverride(name = "lastUpdated", column = @Column (name = "interest_rate_lastUpdated"))
    })
    private Rate interestRate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "interest_accured_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "interest_accured_currency"))

    })
    private Money interestAccrued;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount" , column = @Column(name = "min_balance_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "min_balance_currency"))

    })
    private Money minBalance;

    private Frequency compoundFrequency;


    private Timestamp lastInterestedAppliedAt;




}
