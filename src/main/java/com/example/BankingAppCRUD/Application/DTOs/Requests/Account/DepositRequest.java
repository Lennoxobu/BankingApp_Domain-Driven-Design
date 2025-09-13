package com.example.BankingAppCRUD.Application.DTOs.Requests.Account;


import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter

public class DepositRequest {

    @Nonnull
    private long amount;


}
