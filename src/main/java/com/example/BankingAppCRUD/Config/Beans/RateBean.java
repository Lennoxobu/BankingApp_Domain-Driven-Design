package com.example.BankingAppCRUD.Config.Beans;


import com.example.BankingAppCRUD.Account.Model.baseRate;
import com.example.BankingAppCRUD.Config.Utils.InterestRate.InterestRateService;
import org.springframework.beans.factory.annotation.Autowired;


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
