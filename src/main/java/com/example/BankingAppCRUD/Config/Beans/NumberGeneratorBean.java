package com.example.BankingAppCRUD.Config.Beans;

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

        return (int)(Math.random() * Math.pow(10,4));
    }


    @Bean
    public int generateDebitCardNo () {
        return (int)(Math.random() * Math.pow(10,12));
    }



    @Bean
    public int   generateSafetyKey (String accountNumber ) {

        return Integer.getInteger(UUID.fromString(accountNumber.substring(0, 5)).toString());

    }

    @Bean
    public int  generateSafetyPin () {
        return (int)(Math.random() * Math.pow(10,4));
    }

}
