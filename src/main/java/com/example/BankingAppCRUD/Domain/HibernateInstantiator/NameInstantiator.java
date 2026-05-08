package com.example.BankingAppCRUD.Domain.HibernateInstantiator;

import com.example.BankingAppCRUD.Domain.ValueObject.Name;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;

public class NameInstantiator extends AbstractEmbeddableInstantiator<Name> {


    public NameInstantiator() {
        super(Name.class);


    }

    @Override
    public Object instantiate(ValueAccess valueAccess, SessionFactoryImplementor sessionFactoryImplementor) {
        return Name.builder()
                .first(valueAccess.getValue(0, String.class))
                .last(valueAccess.getValue(1, String.class))
                .knownAs(valueAccess.getValue(2, String.class))
                .build();
    }
}
