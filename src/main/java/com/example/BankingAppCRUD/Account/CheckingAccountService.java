package com.example.BankingAppCRUD.Account;

import com.example.BankingAppCRUD.config.Utils.InterestRate.InterestRateService;
import io.netty.channel.unix.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CheckingAccountService implements AccountService<CheckingAccount> {

    final  CheckingAccountRepository checkingAccountRepository;
    final InterestRateService interestRateService;


    @Autowired

    CheckingAccountService (InterestRateService interestRateService,  CheckingAccountRepository checkingAccountRepository ) {

        this.checkingAccountRepository = checkingAccountRepository;
        this.interestRateService = interestRateService;
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

        account.setAccountNumber(generateAccountNumber());
        account.setDebitCardNo(generateDebitCardNo());
        account.setDebitCardPin(generateDebitCardPin());
        account.setRate(generateRate());


        CheckingAccount checkingAccount = CheckingAccount.builder()
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .accountNumber(account.getAccountNumber())
                .NI(account.getNI())
                .rate(generateRate())
                .rate(account.getRate())
                .balance(account.getBalance())
                .debitCardNo(account.getDebitCardNo())
                .debitCardPin(account.getDebitCardPin())
                .build();

        return this.checkingAccountRepository.save(checkingAccount);

    }

    public boolean createAccounts (List<AccountRequest> accounts ) {
        for (AccountRequest account : accounts) {
            createAccount(account);
        }
        return true;
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


        this.checkingAccountRepository.getReferenceById(Id).setRate(generateRate() + value);
    }

    public int updateByFirstName  (String value , long Id ) {

        return this.checkingAccountRepository.updateByFirstName(value ,Id);

    }


    //Generators , Used to initialise account information

   private  double generateRate() {

        try {
            return this.interestRateService.getInterestRate().block() + baseRate.rate;
        } catch (NullPointerException Ex ) {
            System.out.println(Ex.getMessage());
            return baseRate.rate;

        }


   }

   private int generateDebitCardPin () {

        return (int)(Math.random() * Math.pow(10,4));
    }

    private int generateDebitCardNo () {
        return (int)(Math.random() * Math.pow(10,12));
    }


    private String generateAccountNumber () {
        String uuid = UUID.randomUUID().toString().replaceAll("[^0-9]", "");

        return uuid.substring(0, 12);


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
