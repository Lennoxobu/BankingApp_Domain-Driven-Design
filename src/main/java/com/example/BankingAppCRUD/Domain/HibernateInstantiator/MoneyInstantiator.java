package com.example.BankingAppCRUD.Domain.HibernateInstantiator;

import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;

public class MoneyInstantiator extends AbstractEmbeddableInstantiator<Money> {


    public MoneyInstantiator () {
        super(Money.class);
    }


    @Override
    public Object instantiate(ValueAccess valueAccess, SessionFactoryImplementor sessionFactoryImplementor) {
        return Money.builder()
                .amount(valueAccess.getValue(0 , Long.class))
                .currency(valueAccess.getValue(1, String.class))
                .build();
    }


}
