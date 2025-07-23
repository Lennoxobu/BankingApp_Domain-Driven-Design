package com.example.BankingAppCRUD.Domain.Entity.Transaction.Model;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class FundTransaction {


    @ManyToOne
    private UUID  senderID;

    @ManyToOne
    private UUID  receiverID;
    private Date timeStamp;

    private Double amount;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID Id;


}
