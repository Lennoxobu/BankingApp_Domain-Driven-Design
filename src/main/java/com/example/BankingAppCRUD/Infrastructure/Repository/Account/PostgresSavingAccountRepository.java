package com.example.BankingAppCRUD.Infrastructure.Repository.Account;


import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Ports.AccountRepository;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class PostgresSavingAccountRepository implements AccountRepository<SavingAccount> {

    private final SavingAccountJPARepository savingAccountJPARepository;

    @Autowired
    PostgresSavingAccountRepository (SavingAccountJPARepository savingAccountJPARepository) {
        this.savingAccountJPARepository = savingAccountJPARepository;
    }

    @Override
    public Optional<SavingAccount> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public void save(SavingAccount account) {

    }

    @Override
    public Optional<SavingAccount> updateByBalance(Money amount, UUID id) {
        return Optional.empty();
    }


}
