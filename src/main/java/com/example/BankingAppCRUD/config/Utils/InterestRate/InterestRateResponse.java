package com.example.BankingAppCRUD.config.Utils.InterestRate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InterestRateResponse {
    @JsonProperty("central_bank_rates")
    private List<CentralBankRate> centralBankRates;


}


