package com.example.BankingAppCRUD.Application.DTOs.Requests.Account;


import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class RateRequest {

    @Nonnull
    private double rate;


}
