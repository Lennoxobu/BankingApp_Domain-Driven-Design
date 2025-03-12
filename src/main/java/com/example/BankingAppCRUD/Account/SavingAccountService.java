package com.example.BankingAppCRUD.Account;

import com.example.BankingAppCRUD.config.Utils.InterestRate.InterestRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public class SavingAccountService implements AccountService<SavingAccount> {

    @Autowired
    private final SavingAccountRepository savingAccountRepository;

    @Autowired
    private final InterestRateService interestRateService;


    @Autowired
    SavingAccountService (SavingAccountRepository savingAccountRepository , InterestRateService interestRateService) {
        this.savingAccountRepository = savingAccountRepository;
        this.interestRateService = interestRateService;
    }


    // Saving Account Services Methods
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


    //Rate Business Methods
    @Override
    public double getRate(Long Id ) {
        return this.savingAccountRepository.getReferenceById(Id).getRate();
    }


    @Override
    public void setRate(Long Id, double value) {
        this.savingAccountRepository.getReferenceById(Id).setRate(generateRate());
    }

    private  double generateRate() {

        try {
            return this.interestRateService.getInterestRate().block() + baseRate.rate;
        } catch (NullPointerException Ex ) {
            System.out.println(Ex.getMessage());
            return baseRate.rate;

        }


    }

    // Normal Banking Operations as per Account Service
    @Override
    public int deposit (double value , Long id  ) {

        // Get Account

        SavingAccount account = this.savingAccountRepository.getReferenceById(id);

        value = account.getBalance() + value;

        return this.savingAccountRepository.updateByBalance(value , id);
    }


    @Override
    public int withdraw (double value , Long id ) throws Exception {
        // Get Account
        SavingAccount account  = this.savingAccountRepository.getReferenceById(id);

        if (value <= account.getBalance()) {
            value = account.getBalance() - value;

            return this.savingAccountRepository.updateByBalance(value, id);
        } else {

            throw new Exception();
        }

    }

    @Override
    public int transfer (double value , Long receiverID,  Long grantorID ) throws Exception {
        SavingAccount receiverAcc = this.savingAccountRepository.getReferenceById(receiverID);
        SavingAccount grantorAcc = this.savingAccountRepository.getReferenceById(grantorID);

        if (value <= grantorAcc.getBalance()) {
            this.savingAccountRepository.updateByBalance(grantorAcc.getBalance() - value , grantorID);
            return this.savingAccountRepository.updateByBalance(receiverAcc.getBalance() + value , receiverID);
        } else {
            throw new Exception();
        }
    }



}
