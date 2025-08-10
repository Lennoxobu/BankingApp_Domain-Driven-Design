package com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction;

import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.TransactionStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.TransactionType;
import lombok.Builder;


import java.util.Date;
import java.util.UUID;

@Builder
public record FundTransactionDTO (Money amount, UUID sourceAccountID , UUID destinationAccountID , TransactionStatus status , Date timeStamp , TransactionType type) {
}
