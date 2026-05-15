package com.example.BankingAppCRUD.Infrastructure.Config.InterestRate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
class CentralBankRate {

    @JsonProperty("central_bank_rates")
    private List<CountryBankRate>  centralBankRates;




}

