package com.example.BankingAppCRUD.Domain.ValueObject;


import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.security.Timestamp;

@Data
@Builder
@Embeddable
public class DebitInfo {

    private final int debitCardPin ;
    private final int debitCardNo;
    private final Timestamp expiryDate;
    private final Timestamp issueDate;



    DebitInfo (int debitCardNo, int debitCardPin,Timestamp expiryDate , Timestamp issueDate ) {
        // Need to add some encryption and hashing here
        //..............................
        //...............................
        //................................
    }
}
