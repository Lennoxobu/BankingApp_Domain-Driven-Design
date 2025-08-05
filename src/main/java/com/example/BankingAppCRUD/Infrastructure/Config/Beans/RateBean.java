package com.example.BankingAppCRUD.Infrastructure.Config.Beans;



import com.example.BankingAppCRUD.Infrastructure.Config.InterestRate.InterestRateService;
import org.springframework.beans.factory.annotation.Autowired;


// Cyclic Dependencies Issue
// Research solutions
public class RateBean {

    @Autowired
    private InterestRateService interestRateService;


    RateBean (InterestRateService interestRateService) {this.interestRateService = interestRateService; }

    private  double generateRate() throws Exception {

        try {
            return this.interestRateService.getInterestRate().block();
        } catch (NullPointerException Ex ) {
            System.out.println(Ex.getMessage());

            throw new Exception();

        }


    }
}
