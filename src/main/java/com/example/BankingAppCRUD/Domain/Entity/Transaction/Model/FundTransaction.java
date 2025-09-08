package com.example.BankingAppCRUD.Domain.Entity.Transaction.Model;

import com.example.BankingAppCRUD.Domain.ValueObject.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;


import java.util.Date;
import java.util.UUID;


@Entity
@Getter
@Setter
@ToString
@Builder
@Table(name = "_Transactions")
@Where(clause = "deleted = false")
@AllArgsConstructor
public class FundTransaction {



    @JoinColumns({
            @JoinColumn(name = "_CheckingAccount.id", referencedColumnName = "_CheckingAccount.id"),
            @JoinColumn(name = "_SavingAccount.id", referencedColumnName = "_SavingAccount.id")
    })
    private UUID senderID;


    @JoinColumns({
            @JoinColumn(name = "_CheckingAccount.id", referencedColumnName = "_CheckingAccount.id"),
            @JoinColumn(name = "_SavingAccount.id", referencedColumnName = "_SavingAccount.id")
    })
    private UUID  receiverID;
    private Date timeStamp;

    @Enumerated(EnumType.STRING)
    TransactionType type;

    private Long amount;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;


}
