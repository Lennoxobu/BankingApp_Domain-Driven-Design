package com.example.BankingAppCRUD.Infrastructure.Config.Beans;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NumberGeneratorBean {

    @Bean
    public  String generateAccountNumber () {
        String uuid = UUID.randomUUID().toString().replaceAll("[^0-9]", "");

        return uuid.substring(0, 12);


    }
    @Bean
    public int generateDebitCardPin () {

        return 1000 + (int)(Math.random() * Math.random());
    }


    @Bean
    public long generateDebitCardNo () {
        return (long)(Math.random() * Math.pow(10,12));
    }



    @Bean
    public long generateSortCodeNo () {return 100000 + (int)(Math.random() * 900000); }



    @Bean
    public Integer  generateSafetyKey (String accountNumber ) {

        return Integer.getInteger(accountNumber.substring(0, 5).toString());

    }

    @Bean
    public int  generateSafetyPin () {
        return (int)(Math.random() * Math.pow(10,4));
    }

}
