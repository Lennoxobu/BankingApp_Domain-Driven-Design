package com.example.BankingAppCRUD.Domain.ValueObject;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "accountNo. " ,  column = @Column(name = "accountInfo_account_no" )),
        @AttributeOverride(name = "sortCode", column = @Column(name = "accountInfo_sort_code"))
})
public class AccountInfo {

    private final String accountNo;
    private final long sortCode;


}
