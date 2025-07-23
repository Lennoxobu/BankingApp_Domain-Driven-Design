package com.example.BankingAppCRUD.Domain.Entity.Account.Ports;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository<T extends Account> {



    Optional <T> findById (UUID id );

    void save (T account);


    Optional<T> updateByBalance ( Money amount  , UUID id);
















}
