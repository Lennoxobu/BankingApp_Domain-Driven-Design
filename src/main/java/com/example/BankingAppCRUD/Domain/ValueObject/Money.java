package com.example.BankingAppCRUD.Domain.ValueObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "money_amount" , column = @Column(name = "money_amount")),
        @AttributeOverride(name = "money_currency" , column = @Column(name = "money_currency"))
})
public class Money {

    private final Long amount;
    private final String currency;

}
