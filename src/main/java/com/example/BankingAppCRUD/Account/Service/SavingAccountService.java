package com.example.BankingAppCRUD.Account.Service;

import com.example.BankingAppCRUD.Account.Model.AccountRequest;
import com.example.BankingAppCRUD.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Account.Repository.SavingAccountRepository;
import com.example.BankingAppCRUD.Config.Beans.NumberGeneratorBean;
import com.example.BankingAppCRUD.Config.Utils.InterestRate.InterestRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@Transactional
public class SavingAccountService implements AccountService<SavingAccount> {

    @Autowired
    private final SavingAccountRepository savingAccountRepository;

    @Autowired
    private final InterestRateService interestRateService;


    private final NumberGeneratorBean numberGeneratorBean;



    @Autowired
    SavingAccountService (SavingAccountRepository savingAccountRepository , InterestRateService interestRateService , NumberGeneratorBean numberGeneratorBean) {
        this.savingAccountRepository = savingAccountRepository;
        this.interestRateService = interestRateService;
        this.numberGeneratorBean = numberGeneratorBean;
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


        // SSH needs to  called here
        // debitCardNo needs to be  called  here
        // debitCardPin needs to be   called here

        account.setAccountNumber(this.numberGeneratorBean.generateAccountNumber());
        account.setSafetyID(this.numberGeneratorBean.generateDebitCardNo());
        account.setSafetyKey(this.numberGeneratorBean.generateSafetyKey(account.getAccountNumber()));
        account.setRate(1); // This should call generateRate() but looking for a way to get past the cyclic dependencies



        SavingAccount savingAccount = SavingAccount.builder()
                .accountNumber(account.getAccountNumber())
                .NI(account.getNI())
                .rate(account.getRate())
                .safetyID(account.getSafetyID())
                .safetyKey(account.getSafetyKey())
                .balance(account.getBalance())
                .build();

        return this.savingAccountRepository.save(savingAccount);
    }




    @Override
    public boolean  deleteAccount (SavingAccount account , Long ID ) {
        this.savingAccountRepository.deleteById(ID);

        if (!this.savingAccountRepository.existsById(ID))  return true;


        return false;


    }


    //Rate Business Methods
    @Override
    public double getRate(Long Id ) {
        return this.savingAccountRepository.getReferenceById(Id).getRate();
    }


    @Override
    public void setRate(Long Id, double value) {
        this.savingAccountRepository.getReferenceById(Id).setRate(this.interestRateService.getInterestRate().block(Duration.ofSeconds((long) 0.5)));
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
