package com.example.BankingAppCRUD.Domain.Entity.Account.Model;

import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import com.example.BankingAppCRUD.Domain.ValueObject.DebitInfo;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Table(name= "_CheckingAccount")
@ToString
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SuperBuilder
@Where(clause= "deleted = false" )
@Entity
public class CheckingAccount extends Account {



    @Embedded
    @AttributeOverride(name = "amount" ,  column =  @Column(name = "daily_transaction_limit_amount "))
    @AttributeOverride(name = "currency" , column = @Column(name = "daily_transaction_limit_currency"))
    private Money dailyTransactionLimit ;
    @Embedded
    @AttributeOverride(name = "amount" , column = @Column(name = "monthly_fee_amount"))
    @AttributeOverride(name = "currency" , column = @Column(name = "monthly_fee_currency"))
    private Money monthlyFee;
    @Embedded
    @AttributeOverride(name = "amount" , column = @Column(name = "over_draft_limit_amount"))
    @AttributeOverride(name = "currency" , column = @Column(name = "over_draft_limit_currency"))
    private Money overDraftLimit ;
    @Embedded
    private DebitInfo debitCardInfo;





}
