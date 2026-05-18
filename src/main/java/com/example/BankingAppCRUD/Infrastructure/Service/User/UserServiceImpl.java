package com.example.BankingAppCRUD.Infrastructure.Service.User;

import com.example.BankingAppCRUD.Application.DTOs.Requests.Account.AccountDTO;
import com.example.BankingAppCRUD.Application.DTOs.Requests.User.UserDTO;
//import com.example.BankingAppCRUD.Infrastructure.Config.Security.DTOs.UserResponseWithCredentials;
import com.example.BankingAppCRUD.Application.Exceptions.AccountActionFailedException;
import com.example.BankingAppCRUD.Application.Exceptions.AccountNotActiveException;
import com.example.BankingAppCRUD.Application.Exceptions.AccountNotFoundException;
import com.example.BankingAppCRUD.Application.Exceptions.UserActionFailedException;
import com.example.BankingAppCRUD.Application.Mappers.AccountMapper;
import com.example.BankingAppCRUD.Application.Mappers.UserMapper;
import com.example.BankingAppCRUD.Application.Response.Response;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Domain.Entity.User.Ports.UserService;
import com.example.BankingAppCRUD.Domain.ValueObject.*;
import com.example.BankingAppCRUD.Infrastructure.Config.Beans.NumberGeneratorBean;
import com.example.BankingAppCRUD.Infrastructure.Config.InterestRate.InterestRateService;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.CheckingAccountJPARepository;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.SavingAccountJPARepository;
import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;

import com.example.BankingAppCRUD.Infrastructure.Repository.User.UserJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {


    private final UserJPARepository userJPARepository;
    private final AccountMapper accountMapper;
    private final NumberGeneratorBean numberGeneratorBean;
    private final InterestRateService interestRateService;
    private final UserMapper userMapper;
    private final CheckingAccountJPARepository checkingAccountRepository;
    private final SavingAccountJPARepository savingAccountRepository;


    @Autowired
    UserServiceImpl(UserJPARepository userJPARepository, NumberGeneratorBean numberGeneratorBean
            , InterestRateService interestRateService, CheckingAccountJPARepository checkingAccountRepository,
                    SavingAccountJPARepository savingAccountRepository) {

        this.userJPARepository = userJPARepository;
        this.userMapper = new UserMapper();
        this.accountMapper = new AccountMapper();
        this.numberGeneratorBean = numberGeneratorBean;
        this.interestRateService = interestRateService;
        this.checkingAccountRepository = checkingAccountRepository;
        this.savingAccountRepository = savingAccountRepository;


    }

    @Override
    public Response register(UserDTO dto) throws Exception {


        //Need to implement the password charger method
        User user = User.builder()
                .createdAt(Timestamp.from(Instant.now()))
                .user_address(dto.address())
                .user_email(dto.email())
                .user_roles(List.of(Role.USER))
                .username(dto.userName())
                .lastLoginAt(Timestamp.from(Instant.now()))
                .user_name(Name.builder().first(dto.firstName()).last(dto.lastName()).knownAs(dto.firstName()).build())
                .accountIds(List.of())
                .status(AccountStatus.ACTIVE)
                .build();


        this.userJPARepository.save(user);
        return Response.builder().responseCode("200").message("Success - User account created").build();


    }


    @Override
    public Response deleteUser(UUID id) {


        User user = userJPARepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("User account not found, please make sure user is registered."));


        if (!user.isDeleted()) {
            user.setDeleted(true);

            this.userJPARepository.save(user);
            return Response.builder().responseCode("200").message("Success user removed ").build();
        }

        throw new AccountActionFailedException("User account was either already deleted or operation failed please check user details. ");
    }


    @Override
    public Response createAccount(AccountDTO accountRequest, UUID userId) throws Exception {


        User userAccount = this.userJPARepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found please call this method with a registered user"));


        if (!userAccount.isDeleted() && userAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            String accountType = accountRequest.accountType().toLowerCase();


            if (accountType.equals("checking")) {

                CheckingAccount account = CheckingAccount.builder()
                        .balance(Money.builder().amount(Long.valueOf((long) 0.00)).currency("GBP").build())
                        .dailyTransactionLimit(Money.builder().amount(Long.valueOf((long) 300.00)).currency("GBP").build())
                        .createdAt(Timestamp.from(Instant.now()))
                        .account_status(AccountStatus.ACTIVE)
                        .account_transactions(new ArrayList<>())
                        .info(AccountInfo.builder().accountNo(this.numberGeneratorBean.generateAccountNumber()).sortCode(this.numberGeneratorBean.generateSortCodeNo()).build())
                        .debitCardInfo(DebitInfo.builder().debitCardPin_hashed(String.valueOf(this.numberGeneratorBean.generateDebitCardPin())).debitCardNo_hashed(String.valueOf(this.numberGeneratorBean.generateDebitCardNo()))
                                .expiryDate(Timestamp.from(Instant.now().plusSeconds(315_576_000))) // 10 years from now
                                .issueDate(Timestamp.from(Instant.now()))
                                .build())
                        .rate(Rate.builder().country("UK").lastUpdated(Timestamp.from(Instant.now()))
                                .rateInfo(this.interestRateService.getInterestRate().block(Duration.ofSeconds(2))).build())
                        .monthlyFee(Money.builder().amount((long) 0.10).currency("GBP").build())
                        .overDraftLimit(Money.builder().amount((long) 250.00).currency("GBP").build())
                        .build();

                userAccount.getAccountIds().add(account);
                account.setUser(userAccount);
                this.userJPARepository.save(userAccount);
                this.checkingAccountRepository.save(account);


                return checkingAccountRepository.findById(account.getId()).map(gottenAcc -> Response.builder().responseCode("200")
                        .message("Success - Account created").build()).orElseThrow(Exception::new);

            } else if (accountType.equals("saving")) {
                SavingAccount account = SavingAccount.builder()
                        .account_status(AccountStatus.ACTIVE)
                        .createdAt(Timestamp.from(Instant.now()))
                        .balance(Money.builder().amount(Long.valueOf((long) 0.00)).currency("GBP").build())
                        .info(AccountInfo.builder().accountNo(this.numberGeneratorBean.generateAccountNumber()).sortCode(this.numberGeneratorBean.generateSortCodeNo()).build())
                        .account_transactions(new ArrayList<>())
                        .interestAccrued(Money.builder().amount((long) 300.00).currency("GBP").build())
                        .minBalance(Money.builder().amount((long) 300.00).currency("GBP").build())
                        .rate(Rate.builder().country("UK").lastUpdated(Timestamp.from(Instant.now()))
                                .rateInfo(this.interestRateService.getInterestRate().block(Duration.ofSeconds(2))).build())
                        .interestRate(Rate.builder().country("GBP").lastUpdated(Timestamp.from(Instant.now())).rateInfo(Double.valueOf(0.4)).build())
                        .compoundFrequency(Frequency.YEARLY)
                        .lastInterestedAppliedAt(Timestamp.from(Instant.now()))
                        .build();


                userAccount.getAccountIds().add(account);
                account.setUser(userAccount);
                this.userJPARepository.save(userAccount);
                this.savingAccountRepository.save(account);

                return savingAccountRepository.findById(account.getId()).map(gottenAcc -> Response.builder().responseCode("200")
                        .message("Success - Account created").build()).orElseThrow(Exception::new);


            } else {
                return Response.builder().responseCode("500").message("Error in account creation").build();
            }
        } else {
            return Response.builder().responseCode("500").message("Error in account creation").build();
        }
    }


    @Override
    public Response deleteAccount(UUID id, UUID userId) throws RuntimeException {


        var userAccount = this.userJPARepository.findById(userId)
                .orElseThrow(() -> new AccountNotFoundException("User not found please call this method with a registered user "));


        if (savingAccountRepository.existsById(id)) {

            userAccount.getAccountIds()
                    .forEach(account -> {
                        if (account.getId().equals(id)) {

                            account.setDeleted(true);
                            userAccount.getAccountIds().remove(account);

                            if (!account.isDeleted())
                                throw new AccountActionFailedException("Account action failed: Account maybe deleted or operation failed");
                        }

                    });


        } else if (checkingAccountRepository.existsById(id)) {

            userAccount.getAccountIds()
                    .forEach(account -> {
                        if (account.getId().equals(id)) {

                            account.setDeleted(true);
                            userAccount.getAccountIds().remove(account);

                            if (!account.isDeleted())
                                throw new AccountActionFailedException("Account action failed: Account maybe deleted or operation failed");
                        }

                    });
        } else {
            throw new AccountNotFoundException("Account not found");
        }
        this.userJPARepository.save(userAccount);
        return Response.builder()
                .responseCode("200")
                .message("Account deleted successfully")
                .build();


    }


    @Override
    public Response changeEmail(String value, UUID id) {

        return userJPARepository.findById(id).map(userAccount -> {

            userAccount.setUser_email(value);
            userJPARepository.save(userAccount);
            return Response.builder().responseCode("200").message("Success email changed").build();
        }).orElseThrow(() -> new UserActionFailedException("Error in changing email"));


    }


    @Override
    public Response changeAddress(String value, UUID id) {
        return userJPARepository.findById(id).map(userAccount -> {


            userAccount.setUser_address(value);
            userJPARepository.save(userAccount);
            return Response.builder().responseCode("200").message("Success address changed").build();
        }).orElseThrow(() -> new UserActionFailedException("Error in changing address"));
    }


    @Override
    public Response changeName(UUID id, String firstName, String lastName) {

        if (firstName == null || firstName.length() < 2)
            return Response.builder().responseCode("500").message("Incorrect First name given please check entry").build();


        if (lastName == null || lastName.length() < 2)
            return Response.builder().responseCode("500").message("Incorrect Last name given please check entry").build();


        Name newName = Name.builder().first(firstName).last(lastName).build();

        return userJPARepository.findById(id).map(userAccount -> {
            userAccount.setUser_name(newName);
            userJPARepository.save(userAccount);
            return Response.builder().responseCode("200").message("Success name changed completed").build();

        }).orElseThrow(() -> new UserActionFailedException("Error in changing user name"));


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
    public List<AccountDTO> getAccounts(UUID id) {


        return userJPARepository.findById(id)
                .map(user -> user.getAccountIds().stream()
                        .map(account -> mapAccountIdToDto(account.getId()))
                        .flatMap(Optional::stream)
                        .toList()
                )
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    @Override
    public Response setRole(String value, UUID id) {

        if (value == null || value.isBlank())
            return Response.builder().responseCode("500").message("Role not selected string not passed").build();


        return userJPARepository.findById(id).map(user -> {

            user.setUser_roles(List.of(Role.valueOf(value.toLowerCase()), user.getUser_roles().get(0)));


            return Response.builder().responseCode("200").message("Role change complete").build();
        }).orElseThrow(() -> new RuntimeException("Role not changed error "));


    }


    @Override
    public Response changeStatus(UUID id, String value) {

        if (value == null || value.isBlank())
            return Response.builder().responseCode("500").message("Status not selected string not passed").build();


        return userJPARepository.findById(id).map(user -> {

            user.setStatus(AccountStatus.valueOf(value.toUpperCase()));


            userJPARepository.save(user);

            return Response.builder().responseCode("200").message("Status change complete").build();
        }).orElseThrow(() -> new RuntimeException("Status not changed error"));
    }


//    public UserResponseWithCredentials getUserCredentialsByUsername (String username) throws UserAccountNotFoundException {
//        User user = userJPARepository.findByUsername(username).orElseThrow( () -> new UserAccountNotFoundException("Not found") );
//
//
//        return new UserResponseWithCredentials(this.userMapper.convertToDto(user), user.getHashed_password());
//
//    }


}
