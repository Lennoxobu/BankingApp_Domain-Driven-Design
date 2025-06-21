package com.example.BankingAppCRUD.Domain.ValueObject;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Embeddable
public class Money {

    private final Long amount;
    private final String currency;

}
