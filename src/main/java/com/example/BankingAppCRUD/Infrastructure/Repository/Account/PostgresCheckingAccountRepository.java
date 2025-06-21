package com.example.BankingAppCRUD.Infrastructure.Repository.Account;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Ports.AccountRepository;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class PostgresCheckingAccountRepository implements AccountRepository<CheckingAccount> {


    private final CheckingAccountJPARepository checkingAccountJPARepository;

    @Autowired
    PostgresCheckingAccountRepository (CheckingAccountJPARepository checkingAccountJPARepository) {

        this.checkingAccountJPARepository = checkingAccountJPARepository;
    }

    @Override
    public Optional<CheckingAccount> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public void save(CheckingAccount account) {

    }

    @Override
    public Optional<CheckingAccount> updateByBalance(Money amount, UUID id) {
        return Optional.empty();
    }

    @Override
    public CheckingAccount deleteAccount(UUID id) {
        return null;
    }
}
