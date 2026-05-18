package com.example.BankingAppCRUD.Domain.HibernateInstantiator;

import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;

import java.sql.Timestamp;

public class RateInstantiator extends AbstractEmbeddableInstantiator<Rate> {

    public RateInstantiator() {
        super(Rate.class);
    }


    @Override
    public Object instantiate(ValueAccess valueAccess, SessionFactoryImplementor sessionFactoryImplementor) {
        return Rate.builder()
                .country(valueAccess.getValue(0, String.class))
                .lastUpdated(valueAccess.getValue(1, Timestamp.class))
                .rateInfo(valueAccess.getValue(2, Double.class))
                .build();
    }
}
