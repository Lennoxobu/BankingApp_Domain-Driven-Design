package com.example.BankingAppCRUD.config;


import com.example.BankingAppCRUD.config.Beans.LoggingBean;
import com.example.BankingAppCRUD.config.Beans.NumberGeneratorBean;
import com.example.BankingAppCRUD.config.Beans.RateBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({NumberGeneratorBean.class, RateBean.class, ConfigBeans.class, LoggingBean.class})
public class ConfigBeans {



}
