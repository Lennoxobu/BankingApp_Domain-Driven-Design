package com.example.BankingAppCRUD.Account.Service;

import com.example.BankingAppCRUD.Account.Model.AccountRequest;
import com.example.BankingAppCRUD.Account.Model.AccountResponse;
import com.example.BankingAppCRUD.Account.Repository.CheckingAccountRepository;
import com.example.BankingAppCRUD.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Config.Beans.NumberGeneratorBean;
import com.example.BankingAppCRUD.Config.Utils.InterestRate.InterestRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CheckingAccountService implements AccountService<CheckingAccount> {

    private final CheckingAccountRepository checkingAccountRepository;
   private  final InterestRateService interestRateService;

   private final NumberGeneratorBean numberGeneratorBean;


    @Autowired

    CheckingAccountService (InterestRateService interestRateService,  CheckingAccountRepository checkingAccountRepository, NumberGeneratorBean numberGeneratorBean ) {

        this.checkingAccountRepository = checkingAccountRepository;
        this.interestRateService = interestRateService;
        this.numberGeneratorBean = numberGeneratorBean;
    }


    // Checking Account Services Methods
    @Override
    public Page<CheckingAccount> getAllAccounts (org.springframework.data.domain.Pageable pageable) {
        return this.checkingAccountRepository.findAll(pageable);
    }

    @Override
    public Optional<CheckingAccount> getAccountById(long ID) {
        return this.checkingAccountRepository.findById(ID);
    }

    @Override
    public CheckingAccount createAccount(AccountRequest account ) {

        // SSH needs to  called here
        // debitCardNo needs to be  called  here
        // debitCardPin needs to be   called here

        account.setAccountNumber(this.numberGeneratorBean.generateAccountNumber());
        account.setDebitCardNo(this.numberGeneratorBean.generateDebitCardNo());
        account.setDebitCardPin(this.numberGeneratorBean.generateDebitCardPin());
        account.setRate(1); // This should call generateRate() but looking for a way to get past the cyclic dependencies


        CheckingAccount checkingAccount = CheckingAccount.builder()
                .accountNumber(account.getAccountNumber())
                .NI(account.getNI())
                .rate(account.getRate())
                .balance(account.getBalance())
                .debitCardNo(account.getDebitCardNo())
                .debitCardPin(account.getDebitCardPin())
                .build();

        return this.checkingAccountRepository.save(checkingAccount);

    }

    public int  createAccounts (List<AccountRequest> accounts ) {


        for (AccountRequest account : accounts) {
            createAccount(account);
        }


        return -1;




    }




    @Override
    public boolean  deleteAccount (CheckingAccount account , Long ID ) {
        this.checkingAccountRepository.deleteById(ID);

        return !this.checkingAccountRepository.existsById(ID);


    }


    // Get Rate for an entity on  the business logic level

    @Override
    public double getRate(Long Id ) {
        return this.checkingAccountRepository.getReferenceById(Id).getRate();
    }


    // Setting Rate for an entity on the business logic  level

    @Override
    public void setRate (Long Id  , double value) {


        this.checkingAccountRepository.getReferenceById(Id).setRate( getRate(Id) + value);
    }







    // Normal Banking Operations as per Account Service
    @Override
    public int deposit (double value , Long id  ) {

       // Get Account

        CheckingAccount account = this.checkingAccountRepository.getReferenceById(id);

        value = account.getBalance() + value;

       return this.checkingAccountRepository.updateByBalance(value , id);
    }


    @Override
    public int withdraw (double value , Long id ) throws Exception {
        // Get Account
        CheckingAccount account  = this.checkingAccountRepository.getReferenceById(id);

        if (value <= account.getBalance()) {
            value = account.getBalance() - value;

            return this.checkingAccountRepository.updateByBalance(value, id);
        } else {

            throw new Exception();
        }

    }

    @Override
    public int transfer (double value , Long receiverID,  Long grantorID ) throws Exception {
            CheckingAccount receiverAcc = this.checkingAccountRepository.getReferenceById(receiverID);
            CheckingAccount grantorAcc = this.checkingAccountRepository.getReferenceById(grantorID);

            if (value <= grantorAcc.getBalance()) {
                this.checkingAccountRepository.updateByBalance(grantorAcc.getBalance() - value , grantorID);
                return this.checkingAccountRepository.updateByBalance(receiverAcc.getBalance() + value , receiverID);
            } else {
                throw new Exception();
            }
    }



}
