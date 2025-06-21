package com.example.BankingAppCRUD.Domain.Entity.Account.Model;

import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import com.example.BankingAppCRUD.Domain.ValueObject.DebitInfo;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Table(name= "_CheckingAccount")
@ToString
@Builder
@Entity
public class CheckingAccount extends Account {



    @Embedded
    private Money dailyTransactionLimit ;
    @Embedded
    private Money monthlyFee;
    @Embedded
    private Money overDraftLimit ;
    @Embedded
    private DebitInfo debitCardInfo;


}
