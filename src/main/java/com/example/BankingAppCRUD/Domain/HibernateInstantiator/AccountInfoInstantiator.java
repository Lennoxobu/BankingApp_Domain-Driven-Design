package com.example.BankingAppCRUD.Domain.HibernateInstantiator;

import com.example.BankingAppCRUD.Domain.ValueObject.AccountInfo;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;

public class AccountInfoInstantiator extends AbstractEmbeddableInstantiator<AccountInfo>{


    public AccountInfoInstantiator() {
        super(AccountInfo.class);
    }

    @Override
    public Object instantiate(ValueAccess valueAccess, SessionFactoryImplementor sessionFactoryImplementor) {
        return AccountInfo.builder()
                .accountNo(valueAccess.getValue(0 , String.class))
                .sortCode(valueAccess.getValue(1, Long.class))
                .build();
    }
}
