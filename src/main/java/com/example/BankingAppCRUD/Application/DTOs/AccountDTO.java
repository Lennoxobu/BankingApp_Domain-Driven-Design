package com.example.BankingAppCRUD.Application.DTOs;


import com.example.BankingAppCRUD.Domain.ValueObject.AccountInfo;
import com.example.BankingAppCRUD.Domain.ValueObject.AccountStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;


@Builder

public record AccountDTO(AccountStatus account_Status, AccountInfo account_info, Timestamp created_at, Rate rate, String accountType) {

}
