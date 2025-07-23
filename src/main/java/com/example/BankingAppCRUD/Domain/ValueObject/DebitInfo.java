package com.example.BankingAppCRUD.Domain.ValueObject;


import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
@Embeddable
public class DebitInfo {

    private final String  debitCardPin_hashed ;
    private final  String debitCardNo_hashed ;
    private final Timestamp expiryDate;
    private final  Timestamp issueDate;


}
