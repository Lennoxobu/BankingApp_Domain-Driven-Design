package com.example.BankingAppCRUD.Infrastructure.Service.Account;

import com.example.BankingAppCRUD.Application.Mappers.AccountMapper;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import com.example.BankingAppCRUD.Domain.Entity.Account.Ports.AccountService;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports.FundTransactionService;
import com.example.BankingAppCRUD.Domain.ValueObject.AccountStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.CheckingAccountJPARepository;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Infrastructure.Config.Beans.NumberGeneratorBean;
import com.example.BankingAppCRUD.Infrastructure.Config.InterestRate.InterestRateService;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.SavingAccountJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CheckingAccountServiceImpl implements AccountService<CheckingAccount> {

    private final CheckingAccountJPARepository checkingAccountRepository;
    private  final InterestRateService interestRateService;
    private final NumberGeneratorBean numberGeneratorBean;
    private final FundTransactionService fundTransactionService;
    private final SavingAccountJPARepository savingAccountRepository;
    private final AccountMapper accountMapper = new AccountMapper();



    @Autowired
    CheckingAccountServiceImpl(InterestRateService interestRateService, CheckingAccountJPARepository checkingAccountRepository, NumberGeneratorBean numberGeneratorBean, FundTransactionService fundTransactionService , SavingAccountJPARepository savingAccountRepository) {

        this.checkingAccountRepository = checkingAccountRepository;
        this.interestRateService = interestRateService;
        this.numberGeneratorBean = numberGeneratorBean;
        this.fundTransactionService =  fundTransactionService;
        this.savingAccountRepository = savingAccountRepository;


    }

    @Override
    public Response deposit (long amount , UUID id ) throws Exception {

        if (amount <= 0)
            throw new Exception("Deposit must be greater than zero");

        Optional<CheckingAccount> optionalAccount = this.checkingAccountRepository.findById(id);


        if (optionalAccount.isEmpty())
            return Response.builder().responseCode("404").message("Account not found ").build();




        CheckingAccount account = optionalAccount.get();



        if (account.getAccount_status() != AccountStatus.ACTIVE)
            return Response.builder().responseCode("500").responseCode("Account not active").build();


        Money newAmount = Money.builder().amount(account.getBalance().getAmount() + amount).currency("GBP").build();

        account.setBalance(newAmount);

        try {

            List<UUID>  currList = account.getAccount_transactions();
            currList.add(UUID.fromString(this.fundTransactionService.createTransaction(id, amount, "Deposit").getMessage().toString()));

            account.setAccount_transactions(currList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        this.checkingAccountRepository.save(account);
        return Response.builder().responseCode("200").message("Success").build();


    }


    @Override
    public Response withdraw (long amount , UUID id ) throws Exception {

          return checkingAccountRepository.findById(id).map(account -> {

              if (account.getAccount_status() != AccountStatus.ACTIVE)
                  return Response.builder().responseCode("500").message("Account not active").build();


              if (account.getBalance().getAmount() < amount && account.getDailyTransactionLimit().getAmount()  > amount  )
                  return Response.builder().responseCode("500").message("Not enough funds or you have reached your daily transaction limit ").build();

              Money newValue = Money.builder().currency("GBP").amount(account.getBalance().getAmount() - amount).build();

              account.setBalance(newValue);


              try {

                  List<UUID>  currList = account.getAccount_transactions();
                  currList.add(UUID.fromString(this.fundTransactionService.createTransaction(id, amount, "Withdraw").getMessage().toString()));

                  account.setAccount_transactions(currList);
              } catch (Exception e) {
                  throw new RuntimeException(e);
              }


              this.checkingAccountRepository.save(account);
              return Response.builder().responseCode("200").message("Success").build();



          }).orElseThrow(Exception :: new );



    }




    @Override
    public Response transfer (long value , UUID  receiverId, UUID grantorId ) throws Exception  {


        if (value <= 0 )
            throw new Exception("Please transfer a valid amount");


        Optional<? extends Account> receiverOptionalAccount =  this.checkingAccountRepository.findById(receiverId);
        Optional<? extends Account> grantorOptionalAccount =  this.checkingAccountRepository.findById(grantorId);

        if (receiverOptionalAccount.isEmpty())
            receiverOptionalAccount = this.savingAccountRepository.findById(receiverId);


        if (receiverOptionalAccount.isEmpty())
            return Response.builder().responseCode("404").message("Unable to find receiver account please check account number").build();


        if (grantorOptionalAccount.isEmpty())
            grantorOptionalAccount = this.savingAccountRepository.findById(grantorId);


        if (grantorOptionalAccount.isEmpty())
            return Response.builder().responseCode("404").message("Unable to find grantor account please check account number").build();



        Account reciverAccount = receiverOptionalAccount.get();
        Account grantorAccount =  grantorOptionalAccount.get();

        withdraw(value , grantorId);
        deposit(value , receiverId);

        this.fundTransactionService.createTransaction(receiverId, grantorId, value);

        return Response.builder().responseCode("200").message("Success").build();


    }


    @Override
    public Response viewBalance (UUID id ) throws Exception  {

       return checkingAccountRepository.findById(id).map(account -> {

           if (account.getAccount_status() != AccountStatus.ACTIVE)
               return Response.builder().responseCode("400").message("Account is not active").build();

           return Response.builder().responseCode("200").message(account.getBalance().toString()).build();

       }).orElseThrow(Exception :: new );


    }




    @Override
    public Response setRate (UUID id , double value ) throws Exception {

        return checkingAccountRepository.findById(id).map(account -> {

            Rate baseRate = account.getRate();

            Rate adjustedRate = Rate.builder()
                    .rateInfo(baseRate.getRateInfo() * value)
                    .country(baseRate.getCountry())
                    .lastUpdated(baseRate.getLastUpdated())
                    .build();

            return Response.builder().responseCode("200").message("Success new rate" + adjustedRate.getRateInfo() + "%").build();




        }).orElseThrow(Exception :: new );





    }


    @Override
    public Response updateAccountStatus  (String selection ,  UUID id  ) throws Exception  {

        if (selection == null || selection.isBlank())
            throw new Exception("Account not found");


        AccountStatus newStatus;


        try {

            newStatus = AccountStatus.valueOf(selection.toUpperCase());

        } catch (Exception e) {
            return Response.builder().responseCode("400").message("account status not found").build();
        }

        return  checkingAccountRepository.findById(id).map(account -> {

            if (account == null || account.getAccount_status() != AccountStatus.ACTIVE)
                return Response.builder().responseCode("404").message("account not found").build();

            account.setAccount_status(AccountStatus.valueOf(selection));

            this.checkingAccountRepository.save(account);

            return Response.builder().responseCode("200").message("Success account status changed").build();


        }).orElse(null);





    }




    public Response applyRate (UUID id ) throws Exception {



            return  checkingAccountRepository.findById(id).map(account -> {


                      long ratedValue =  account.getBalance().getAmount() * account.getRate().getRateInfo().longValue();


                       Money rateAppliedBalance = Money.builder().amount(account.getBalance().getAmount() + ratedValue).build();
                        account.setBalance(rateAppliedBalance);


                return Response.builder().responseCode("200").message("Rate applied").build();


            }).orElseThrow(Exception :: new );




        }







}
