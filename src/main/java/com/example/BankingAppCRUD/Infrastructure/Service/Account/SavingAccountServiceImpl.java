package com.example.BankingAppCRUD.Infrastructure.Service.Account;

import com.example.BankingAppCRUD.Application.Mappers.AccountMapper;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Ports.AccountService;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports.FundTransactionService;
import com.example.BankingAppCRUD.Domain.ValueObject.AccountStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.Frequency;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.CheckingAccountJPARepository;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.SavingAccountJPARepository;
import com.example.BankingAppCRUD.Infrastructure.Config.Beans.NumberGeneratorBean;
import com.example.BankingAppCRUD.Infrastructure.Config.InterestRate.InterestRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SavingAccountServiceImpl implements AccountService<SavingAccount> {



    private final SavingAccountJPARepository savingAccountRepository;
    private final CheckingAccountJPARepository checkingAccountRepository;
    private final InterestRateService interestRateService;
    private final AccountMapper accountMapper =  new AccountMapper();
    private final FundTransactionService fundTransactionService;



    @Autowired
    SavingAccountServiceImpl(SavingAccountJPARepository savingAccountRepository , InterestRateService interestRateService , NumberGeneratorBean numberGeneratorBean  , FundTransactionService fundTransactionService,
    CheckingAccountJPARepository checkingAccountRepository) {
        this.savingAccountRepository = savingAccountRepository;
        this.interestRateService = interestRateService;
        this.fundTransactionService = fundTransactionService;
        this.checkingAccountRepository = checkingAccountRepository;
    }


    @Override
    public Response deposit (long amount , UUID id ) throws Exception {

       if (amount <= 0 )
            throw new Exception("Deposit must be greater than zero");

       Optional<SavingAccount>  optionalAccount = this.savingAccountRepository.findById(id);

        if (optionalAccount.isEmpty())
                return  Response.builder().responseCode("404").message("Account not found").build();


        SavingAccount account = optionalAccount.get();

        if (account.getAccount_status() != AccountStatus.ACTIVE)
            throw new Exception("Account not active");

        this.savingAccountRepository.save(account);
        this.fundTransactionService.createTransaction( account.getId() , amount , "Deposit");

        return Response.builder().responseCode("200").message("Success").build();




    }


    @Override
    public Response withdraw (long amount , UUID id ) throws Exception {

        Optional<SavingAccount> optionalAccount =  this.savingAccountRepository.findById(id);

        if (optionalAccount.isEmpty())
                return Response.builder().responseCode("404").message("Account not found ").build();

        SavingAccount account = optionalAccount.get();

        if (account.getAccount_status() != AccountStatus.ACTIVE)
            throw new Exception("Account not active");

        if (account.getMinBalance().getAmount() < amount || account.getBalance().getAmount() < amount )
                return Response.builder().responseCode("401").message("Not enough funds you must maintenance min balance in your saving account ").build();


        Money newValue = Money.builder().currency("GBP").amount(account.getBalance().getAmount() - amount).build();

        account.setBalance(newValue);


        this.savingAccountRepository.save(account);

        this.fundTransactionService.createTransaction(account.getId() , amount, "Withdraw");


        return Response.builder().responseCode("200").message("Success").build();


    }




    @Override
    public Response transfer (long value , UUID  receiverId, UUID grantorId ) throws Exception  {


        if (value <= 0 )
            throw new Exception("Please transfer a valid amount");


        Optional<? extends Account> receiverOptionalAccount =  this.savingAccountRepository.findById(receiverId);
        Optional<? extends Account> grantorOptionalAccount =  this.savingAccountRepository.findById(grantorId);

        if (receiverOptionalAccount.isEmpty())
            receiverOptionalAccount = this.checkingAccountRepository.findById(receiverId);


        if (receiverOptionalAccount.isEmpty())
            return Response.builder().responseCode("404").message("Unable to find receiver account please check account number").build();


        if (grantorOptionalAccount.isEmpty())
            grantorOptionalAccount = this.checkingAccountRepository.findById(grantorId);


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

        Optional<SavingAccount> optionalAccount = this.savingAccountRepository.findById(id);

        if (optionalAccount.isEmpty())
            return Response.builder().responseCode("404").message("Account not found ").build();



        SavingAccount account = optionalAccount.get();


        if (account.getAccount_status() != AccountStatus.ACTIVE)
                return Response.builder().responseCode("400").message("Account is not active").build();

        return Response.builder().responseCode("200").message(account.getBalance().toString()).build();

    }




    @Override
    public Response setRate (UUID id , double value ) throws Exception {

        Optional<SavingAccount> optionalAccount = this.savingAccountRepository.findById(id);

        if (optionalAccount.isEmpty())
            return Response.builder().responseCode("404").message("Account not found").build();


        SavingAccount account = optionalAccount.get();



        Rate baseRate = account.getRate();

        Rate adjustedRate = Rate.builder()
                .rateInfo(baseRate.getRateInfo() * value)
                .country(baseRate.getCountry())
                .lastUpdated(baseRate.getLastUpdated())
                .build();

        return Response.builder().responseCode("200").message("Success new rate" + adjustedRate.getRateInfo() + "%").build();

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

                return  savingAccountRepository.findById(id).map(account -> {

                    if (account == null || account.getAccount_status() != AccountStatus.ACTIVE)
                        return Response.builder().responseCode("404").message("account not found").build();

                    account.setAccount_status(AccountStatus.valueOf(selection));

                    this.savingAccountRepository.save(account);

                    return Response.builder().responseCode("200").message("Success account status changed").build();


                }).orElse(null);


    }




    public Response updateCompoundFrequency (String selection , UUID Id ) throws  Exception {

        if (selection == null || selection.isBlank())
            throw new Exception("Account not found");


        Frequency newFrequency;


        try {

            newFrequency = Frequency.valueOf(selection.toUpperCase());

        } catch (Exception e) {
            return Response.builder().responseCode("400").message("account status not found").build();
        }


      return savingAccountRepository.findById(Id).map(account -> {
            if (account == null || account.getAccount_status() != AccountStatus.ACTIVE)
                return Response.builder().responseCode("404").message("account not found").build();

            account.setCompoundFrequency(Frequency.valueOf(selection));
            this.savingAccountRepository.save(account);

          return Response.builder().responseCode("200").message("Success account status changed").build();
        }).orElseThrow( Exception :: new );

    }


    public Response applyRate (UUID id ) throws Exception {

        return  savingAccountRepository.findById(id).map(account -> {

            Timestamp timeLimit;
            long ratedValue;
            Money rateAppliedBalance;

           switch (account.getCompoundFrequency()){
                case DAILY:

                    timeLimit  = Timestamp.valueOf(account.getLastInterestedAppliedAt().toLocalDateTime().minusDays(1));
                   ratedValue =  account.getBalance().getAmount() * account.getRate().getRateInfo().longValue();
                    account.setInterestAccrued(
                            Money.builder().amount(account.getInterestAccrued().getAmount() + ratedValue)
                                    .currency("GBP")
                                    .build()
                    );

                   rateAppliedBalance = Money.builder().amount(account.getBalance().getAmount() + ratedValue).build();
                    account.setBalance(rateAppliedBalance);

                    break;

               case  MONTHLY:
                   timeLimit  = Timestamp.valueOf(account.getLastInterestedAppliedAt().toLocalDateTime().minusMonths(1));
                   ratedValue =  account.getBalance().getAmount() * account.getRate().getRateInfo().longValue();
                   account.setInterestAccrued(
                           Money.builder().amount(account.getInterestAccrued().getAmount() + ratedValue)
                                   .currency("GBP")
                                   .build()
                   );

                  rateAppliedBalance = Money.builder().amount(account.getBalance().getAmount() + ratedValue).build();
                   account.setBalance(rateAppliedBalance);


                   break;



               case QUARTERLY:

                   timeLimit  = Timestamp.valueOf(account.getLastInterestedAppliedAt().toLocalDateTime().minusMonths(4));
                   ratedValue =  account.getBalance().getAmount() * account.getRate().getRateInfo().longValue();
                   account.setInterestAccrued(
                           Money.builder().amount(account.getInterestAccrued().getAmount() + ratedValue)
                                   .currency("GBP")
                                   .build()
                   );

                   rateAppliedBalance = Money.builder().amount(account.getBalance().getAmount() + ratedValue).build();
                   account.setBalance(rateAppliedBalance);

                   break;

               case WEEKLY:

                   timeLimit  = Timestamp.valueOf(account.getLastInterestedAppliedAt().toLocalDateTime().minusDays(7));
                   ratedValue =  account.getBalance().getAmount() * account.getRate().getRateInfo().longValue();
                   account.setInterestAccrued(
                           Money.builder().amount(account.getInterestAccrued().getAmount() + ratedValue)
                                   .currency("GBP")
                                   .build()
                   );

                   rateAppliedBalance = Money.builder().amount(account.getBalance().getAmount() + ratedValue).build();
                   account.setBalance(rateAppliedBalance);
                   break;

               default:

                   timeLimit  = Timestamp.valueOf(account.getLastInterestedAppliedAt().toLocalDateTime().minusYears(1));
                   ratedValue =  account.getBalance().getAmount() * account.getRate().getRateInfo().longValue();
                   account.setInterestAccrued(
                           Money.builder().amount(account.getInterestAccrued().getAmount() + ratedValue)
                                   .currency("GBP")
                                   .build()
                   );

                   rateAppliedBalance = Money.builder().amount(account.getBalance().getAmount() + ratedValue).build();
                   account.setBalance(rateAppliedBalance);
            }

            return Response.builder().responseCode("200").message("Rate applied").build();


        }).orElseThrow(Exception :: new );



    }







}
