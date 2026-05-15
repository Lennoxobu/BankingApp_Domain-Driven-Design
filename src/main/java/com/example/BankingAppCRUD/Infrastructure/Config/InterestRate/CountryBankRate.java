package com.example.BankingAppCRUD.Infrastructure.Config.InterestRate;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CountryBankRate {

    @JsonProperty("central_bank")
    private String centralBank;

    @JsonProperty("country")
    private String country;

    @JsonProperty("rate_pct")
    private double ratePct;

    @JsonProperty("last_updated")
    private String lastUpdated;
}
