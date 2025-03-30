package com.example.BankingAppCRUD.config.Beans;


import com.example.BankingAppCRUD.Account.baseRate;
import com.example.BankingAppCRUD.config.Utils.InterestRate.InterestRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;




// Cyclic Dependencies Issue
// Research solutions
public class RateBean {

    @Autowired
    private InterestRateService interestRateService;


    RateBean (InterestRateService interestRateService) {this.interestRateService = interestRateService; }

    private  double generateRate() {

        try {
            return this.interestRateService.getInterestRate().block() + baseRate.rate;
        } catch (NullPointerException Ex ) {
            System.out.println(Ex.getMessage());
            return baseRate.rate;

        }


    }
}
