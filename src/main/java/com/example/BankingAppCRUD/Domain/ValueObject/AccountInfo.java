package com.example.BankingAppCRUD.Domain.ValueObject;


import com.example.BankingAppCRUD.Domain.HibernateInstantiator.AccountInfoInstantiator;
import com.example.BankingAppCRUD.Domain.HibernateInstantiator.MoneyInstantiator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.EmbeddableInstantiator;

import java.util.UUID;

@Data
@Builder
@Embeddable
@AllArgsConstructor
@AttributeOverrides({
        @AttributeOverride(name = "accountNo. " ,  column = @Column(name = "accountInfo_account_no" )),
        @AttributeOverride(name = "sortCode", column = @Column(name = "accountInfo_sort_code"))
})
@EmbeddableInstantiator(AccountInfoInstantiator.class)
public class AccountInfo {

    private final String accountNo;
    private final long sortCode;


}
