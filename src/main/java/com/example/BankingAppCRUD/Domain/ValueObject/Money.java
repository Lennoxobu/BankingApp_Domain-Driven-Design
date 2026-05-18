package com.example.BankingAppCRUD.Domain.ValueObject;

import com.example.BankingAppCRUD.Domain.HibernateInstantiator.MoneyInstantiator;
import com.example.BankingAppCRUD.Domain.HibernateInstantiator.NameInstantiator;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.EmbeddableInstantiator;

@Data
@Builder
@Embeddable
@AllArgsConstructor
@AttributeOverrides({
        @AttributeOverride(name = "money_amount" , column = @Column(name = "money_amount")),
        @AttributeOverride(name = "money_currency" , column = @Column(name = "money_currency"))
})
@EmbeddableInstantiator(MoneyInstantiator.class)
public class Money {

    private final Long amount;
    private final String currency;

}
