package com.example.BankingAppCRUD.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public class SavingAccountService implements AccountService<SavingAccount> {

    @Autowired
    private final SavingAccountRepository savingAccountRepository;


    @Autowired
    SavingAccountService (SavingAccountRepository savingAccountRepository) {
        this.savingAccountRepository = savingAccountRepository;
    }


    // Checking Account Services Methods
    @Override
    public Page<SavingAccount> getAllAccounts (Pageable pageable) {
        return this.savingAccountRepository.findAll(pageable);
    }

    @Override
    public Optional<SavingAccount> getAccountById(long ID) {
        return this.savingAccountRepository.findById(ID);
    }

    @Override
    public SavingAccount createAccount(AccountRequest account ) {

        return null;
    }




    @Override
    public boolean  deleteAccount (SavingAccount account , Long ID ) {
        this.savingAccountRepository.deleteById(ID);

        if (!this.savingAccountRepository.existsById(ID))
            return true;


        return false;


    }

}
