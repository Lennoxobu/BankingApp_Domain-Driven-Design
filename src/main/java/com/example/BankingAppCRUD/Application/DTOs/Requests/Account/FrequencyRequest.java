package com.example.BankingAppCRUD.Application.DTOs.Requests.Account;


import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrequencyRequest {


    @Nonnull
    private String frequency;


}
