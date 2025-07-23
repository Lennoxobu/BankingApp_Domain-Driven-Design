package com.example.BankingAppCRUD.Application.DTOs;

import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.TransactionStatus;
import lombok.Builder;


import java.util.Date;
import java.util.UUID;

@Builder
public record FundTransactionDTO (Money amount, UUID sourceAccountID , UUID destinationAccountID , TransactionStatus status , Date timeStamp ) {
}
