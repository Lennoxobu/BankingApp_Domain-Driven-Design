package com.example.BankingAppCRUD.Domain.Entity.Transaction.Model;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import com.example.BankingAppCRUD.Domain.ValueObject.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@Table(name = "_Transactions")
@AllArgsConstructor
public class FundTransaction {


    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "_CheckingAccount.id", referencedColumnName = "_CheckingAccount.id"),
            @JoinColumn(name = "_SavingAccount.id", referencedColumnName = "_SavingAccount.id")
    })
    private UUID  senderID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "_CheckingAccount.id", referencedColumnName = "_CheckingAccount.id"),
            @JoinColumn(name = "_SavingAccount.id", referencedColumnName = "_SavingAccount.id")
    })
    private UUID  receiverID;
    private Date timeStamp;

    @Enumerated(EnumType.STRING)
    TransactionType type;

    private Double amount;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID Id;


}
