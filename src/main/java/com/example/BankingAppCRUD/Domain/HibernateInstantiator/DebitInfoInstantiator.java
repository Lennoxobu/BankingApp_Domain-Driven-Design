package com.example.BankingAppCRUD.Domain.HibernateInstantiator;

import com.example.BankingAppCRUD.Domain.ValueObject.DebitInfo;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;

import java.sql.Timestamp;

public class DebitInfoInstantiator extends AbstractEmbeddableInstantiator<DebitInfo>{

    public DebitInfoInstantiator() {
        super(DebitInfo.class);
    }


    @Override
    public Object instantiate(ValueAccess valueAccess, SessionFactoryImplementor sessionFactoryImplementor) {
        return DebitInfo.builder()
                .debitCardPin_hashed(valueAccess.getValue(0 , String.class))
                .debitCardNo_hashed(valueAccess.getValue(1, String.class))
                .expiryDate(valueAccess.getValue(2, Timestamp.class))
                .issueDate(valueAccess.getValue(3, Timestamp.class))
                .build();
    }
}
