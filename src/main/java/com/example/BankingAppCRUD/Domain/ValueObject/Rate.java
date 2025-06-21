package com.example.BankingAppCRUD.Domain.ValueObject;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;


@Data
@Builder
@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "country" , column = @Column(name = "rate_country")),
        @AttributeOverride(name = "rateInfo" , column  = @Column(name = "rate_info")),
        @AttributeOverride(name = "lastUpdated" , column = @Column (name = "rate_lastUpdated"))
})
public class Rate {


    private final String country;
    private final Double rateInfo;
    private final Timestamp lastUpdated;



}
