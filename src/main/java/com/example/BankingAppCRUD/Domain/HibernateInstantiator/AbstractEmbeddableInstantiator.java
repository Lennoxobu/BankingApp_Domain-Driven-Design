package com.example.BankingAppCRUD.Domain.HibernateInstantiator;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.EmbeddableInstantiator;


public abstract class AbstractEmbeddableInstantiator <T> implements EmbeddableInstantiator {


    private final Class<T> type;


    protected AbstractEmbeddableInstantiator(Class<T> type) {
        this.type = type;
    }


    @Override
    public boolean isInstance(Object object, SessionFactoryImplementor sessionFactoryImplementor) {
        return type.isInstance(object);
    }

    @Override
    public boolean isSameClass(Object object, SessionFactoryImplementor sessionFactoryImplementor) {
        return object.getClass().equals(type);
    }
}
