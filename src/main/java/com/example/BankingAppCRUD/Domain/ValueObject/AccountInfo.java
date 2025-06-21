package com.example.BankingAppCRUD.Domain.ValueObject;


import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Embeddable
public class AccountInfo {

    private final  String accountNo;
    private final int sortCode;


}
