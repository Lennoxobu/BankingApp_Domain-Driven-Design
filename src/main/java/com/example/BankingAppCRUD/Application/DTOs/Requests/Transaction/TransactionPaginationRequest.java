package com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction;


import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.TransactionStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPaginationRequest {

    private Money amount;
    private UUID sourceAccountID ;
    private UUID destinationAccountID ;
    private TransactionStatus status ;
    private Date timeStamp ;
    private TransactionType type;
    private Integer page;
    private Integer size;
    private String sort;

}
