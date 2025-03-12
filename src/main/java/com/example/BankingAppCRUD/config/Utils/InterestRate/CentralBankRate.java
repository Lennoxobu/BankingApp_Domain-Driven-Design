package com.example.BankingAppCRUD.config.Utils.InterestRate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
class CentralBankRate {
    @JsonProperty("central_bank")
    private String centralBank;

    @JsonProperty("country")
    private String country;

    @JsonProperty("rate_pct")
    private double ratePct;

    @JsonProperty("last_updated")
    private String lastUpdated;


}

