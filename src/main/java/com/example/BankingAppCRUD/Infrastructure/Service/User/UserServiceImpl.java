package com.example.BankingAppCRUD.Infrastructure.Service.User;

import com.example.BankingAppCRUD.Application.DTOs.AccountDTO;
import com.example.BankingAppCRUD.Application.DTOs.UserDTO;
import com.example.BankingAppCRUD.Application.Mappers.AccountMapper;
import com.example.BankingAppCRUD.Application.Mappers.UserMapper;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Domain.Entity.User.Ports.UserService;
import com.example.BankingAppCRUD.Domain.ValueObject.*;
import com.example.BankingAppCRUD.Infrastructure.Config.Beans.NumberGeneratorBean;
import com.example.BankingAppCRUD.Infrastructure.Config.InterestRate.InterestRateService;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.CheckingAccountJPARepository;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.SavingAccountJPARepository;
import com.example.BankingAppCRUD.Infrastructure.Service.Account.CheckingAccountServiceImpl;
import com.example.BankingAppCRUD.Infrastructure.Service.Account.SavingAccountServiceImpl;
import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;

import com.example.BankingAppCRUD.Infrastructure.Repository.User.UserJPARepository;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {


    private final UserJPARepository userJPARepository;
    private final CheckingAccountServiceImpl checkingAccountServiceImpl;
    private final SavingAccountServiceImpl savingAccountServiceImpl;
    private final AccountMapper accountMapper;
    private final NumberGeneratorBean numberGeneratorBean;
    private final InterestRateService interestRateService;
    private final UserMapper userMapper;
    private final CheckingAccountJPARepository checkingAccountRepository;
    private final SavingAccountJPARepository savingAccountRepository;



    @Autowired
    UserServiceImpl(UserJPARepository userJPARepository, CheckingAccountServiceImpl checkingAccountServiceImpl,
                    SavingAccountServiceImpl savingAccountServiceImpl, NumberGeneratorBean numberGeneratorBean
                , InterestRateService interestRateService, CheckingAccountJPARepository checkingAccountRepository,
                    SavingAccountJPARepository savingAccountRepository) {

        this.userJPARepository = userJPARepository;
        this.checkingAccountServiceImpl = checkingAccountServiceImpl;
        this.savingAccountServiceImpl = savingAccountServiceImpl;
        this.userMapper = new UserMapper();
        this.accountMapper =  new AccountMapper();
        this.numberGeneratorBean = numberGeneratorBean;
        this.interestRateService = interestRateService;
        this.checkingAccountRepository = checkingAccountRepository;
        this.savingAccountRepository = savingAccountRepository;




    }

    @Override
    public Response register (UserDTO dto) throws Exception {


        //Need to implement the password charger method
        User user = User.builder().user_id(UUID.randomUUID())
                .createdAt(Timestamp.from(Instant.now()))
                .user_address(dto.address())
                .user_email(dto.email())
                .user_role(Role.USER)
                .user_username(dto.userName())
                .lastLoginAt(Timestamp.from(Instant.now()))
                .user_name(Name.builder().first(dto.firstName()).last(dto.lastName()).knownAs(dto.firstName()).build())
                .accountIds(List.of(null))
                .status(AccountStatus.ACTIVE)
                .build();


        this.userJPARepository.save(user);


        return Response.builder().responseCode("200").message("Success - User account created").build();



    }




    @Override
    public Response  deleteUser ( UUID id ) {



        this.userJPARepository.deleteById(id);

        if (!this.userJPARepository.existsById(id)) {
            return Response.builder().responseCode("200").message("Success user removed ").build();
        } else {
            return Response.builder().responseCode("500").message("Error with deleting user").build();
        }

    }




    @Override
    public Response createAccount  (AccountDTO accountRequest) throws Exception {


        String accountType = accountRequest.accountType().toString().toLowerCase();


        if (accountType == "checking") {


            CheckingAccount account =  CheckingAccount.builder()
                    .balance(Money.builder().currency("GBP").amount(Long.valueOf((long) 0.00)).build())
                    .dailyTransactionLimit(Money.builder().currency("GBP").amount(Long.valueOf((long) 300.00)).build())
                    .createdAt(Timestamp.from(Instant.now()))
                    .account_status(AccountStatus.ACTIVE)
                    .id(UUID.randomUUID())
                    .account_transactions(List.of(null))
                    .info(AccountInfo.builder().accountNo(this.numberGeneratorBean.generateAccountNumber()).sortCode(this.numberGeneratorBean.generateSortCodeNo()).build())
                    .debitCardInfo(DebitInfo.builder().debitCardNo_hashed(String.valueOf(this.numberGeneratorBean.generateDebitCardNo())).debitCardPin_hashed(String.valueOf(this.numberGeneratorBean.generateDebitCardPin())).issueDate(Timestamp.from(Instant.now()))
                            .expiryDate(Timestamp.from(Instant.MAX)).build())
                    .rate(Rate.builder().country("UK").rateInfo(this.interestRateService.getInterestRate().block(Duration.ofSeconds(1)))
                            .lastUpdated(Timestamp.from(Instant.now())).build())
                    .monthlyFee(Money.builder().currency("GBP").amount((long) 0.10).build())
                    .overDraftLimit(Money.builder().currency("GBP").amount((long) 250.00).build())
                    .build();
            this.checkingAccountRepository.save(account);

            return checkingAccountRepository.findById(account.getId()).map(gottenAcc -> Response.builder().responseCode("200")
                    .message("Success - Account created").build()).orElseThrow(Exception :: new );
        } else if (accountType == "saving") {
            SavingAccount account = SavingAccount.builder()
                    .id(UUID.randomUUID())
                    .rate(Rate.builder().rateInfo(this.interestRateService.getInterestRate().block(Duration.ofSeconds(1))).country("UK").lastUpdated(Timestamp.from(Instant.now())).build())
                    .account_status(AccountStatus.ACTIVE)
                    .createdAt(Timestamp.from(Instant.now()))
                    .balance(Money.builder().currency("GBP").amount(Long.valueOf((long) 0.00)).build())
                    .info(AccountInfo.builder().accountNo(this.numberGeneratorBean.generateAccountNumber()).sortCode(this.numberGeneratorBean.generateSortCodeNo()).build())
                    .account_transactions(List.of(null))
                    .interestAccrued(Money.builder().currency("GBP").amount((long)300.00).build())
                    .minBalance(Money.builder().currency("GBP").amount((long)300.00).build())
                    .compoundFrequency(Frequency.YEARLY)
                    .lastInterestedAppliedAt(Timestamp.from(Instant.now()))
                    .build();


            this.savingAccountRepository.save(account);

            return savingAccountRepository.findById(account.getId()).map(gottenAcc -> Response.builder().responseCode("200")
                    .message("Success - Account created").build()).orElseThrow(Exception :: new);


        } else {
            return Response.builder().responseCode("500").message("Error in account creation").build();
        }


    }


    @Override
    public Response deleteAccount(UUID id) {

        boolean deleted = false;

        if (savingAccountRepository.existsById(id)) {
            savingAccountRepository.deleteById(id);
            deleted = true;
        }

        if (checkingAccountRepository.existsById(id)) {
            checkingAccountRepository.deleteById(id);
            deleted = true;
        }

        if (deleted) {
            return Response.builder()
                    .responseCode("200")
                    .message("Account deleted successfully")
                    .build();
        } else {
            return Response.builder()
                    .responseCode("404")
                    .message("Account not found")
                    .build();
        }
    }


    @Override
    public Response changeEmail (String value, UUID id ) {

        return userJPARepository.findById(id).map(account -> {

            account.setUser_email(value);
            return Response.builder().responseCode("200").message("Success email changed").build();
        }).orElse(Response.builder().responseCode("400").message("Error is changing email").build());




    }



    @Override
    public Response changeAddress (String value, UUID id ) {
        return userJPARepository.findById(id).map(account -> {
            account.setUser_address(value);
            return Response.builder().responseCode("200").message("Success address changed").build();
        }).orElse(Response.builder().responseCode("500").message("Error in changing address").build());
    }


    @Override
    public Response changeName (UUID id , String firstName , String lastName ) {

        if (firstName ==  null  || firstName.length() < 2)
            return Response.builder().responseCode("500").message("Incorrect First name given please check entry").build();


        if (lastName == null ||  lastName.length() < 2 )
                return Response.builder().responseCode("500").message("Incorrect Last name given please check entry").build();


        Name newName = Name.builder().first(firstName).last(lastName).build();

       return  userJPARepository.findById(id).map(user -> {
            user.setUser_name(newName);

            return Response.builder().responseCode("200").message("Success name changed completed").build();

        }).orElse(Response.builder().responseCode("500").message("Error in changing name of user please recheck user details").build());


    }

    // Helper Method
    private Optional<AccountDTO> mapAccountIdToDto(UUID accountId) {
        return checkingAccountRepository.findById(accountId)
                .map(accountMapper::convertToDto)
                .or(() -> savingAccountRepository.findById(accountId)
                        .map(accountMapper::convertToDto)
                );
    }

    @Override
    public List<AccountDTO> getAccounts (UUID id ) {


        return userJPARepository.findById(id)
                .map(user -> user.getAccountIds().stream()
                        .map(this::mapAccountIdToDto)
                        .flatMap(Optional::stream)
                        .toList()
                )
                .orElseThrow(() -> new RuntimeException("User not found"));
    }




    @Override
    public Response setRole (String value , UUID id ) {

        if (value == null || value.isBlank())
                return Response.builder().responseCode("500").message("Role not selected string not passed").build();


        return userJPARepository.findById(id).map(user -> {

            user.setUser_role(Role.valueOf(value.toLowerCase()));


            return Response.builder().responseCode("200").message("Role change complete").build();
        }).orElseThrow(() -> new RuntimeException("Role not changed error "));


    }





    @Override
    public Response changeStatus (UUID id ,String value ) {

        if (value == null || value.isBlank())
            return Response.builder().responseCode("500").message("Status not selected string not passed").build();


        return userJPARepository.findById(id).map(user -> {

            user.setStatus(AccountStatus.valueOf(value.toLowerCase()));


            return Response.builder().responseCode("200").message("Status change complete").build();
        }).orElseThrow(() -> new RuntimeException("Status not changed error"));
    }

    


}
