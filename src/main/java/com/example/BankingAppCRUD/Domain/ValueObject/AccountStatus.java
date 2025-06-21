package com.example.BankingAppCRUD.Domain.ValueObject;

import jakarta.persistence.Embeddable;


public enum AccountStatus {

    FROZEN,
    ACTIVE,
    DISABLED,
    LOCKED

}
