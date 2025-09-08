package com.example.BankingAppCRUD.Domain.ValueObject;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
@Embeddable
@AttributeOverrides( {
        @AttributeOverride(name = "debit_info_cardPin", column = @Column(name = "debit_info_cardPin")),
        @AttributeOverride(name = "debit_info_cardNo" , column = @Column (name = "debit_info_cardNo")),
        @AttributeOverride(name = "debit_info_expiryDate" , column = @Column(name = "debit_info_expiryDate" )),
        @AttributeOverride(name = "debit_info_issueDate",  column = @Column (name = "debit_info_issueDate"))
})
public class DebitInfo {

    private final String debitCardPin_hashed ;
    private final  String debitCardNo_hashed ;
    private final Timestamp expiryDate;
    private final  Timestamp issueDate;


}
