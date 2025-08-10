package com.example.BankingAppCRUD.Application.Controllers.AccountController;

import com.example.BankingAppCRUD.Application.DTOs.Requests.Account.AccountDTO;
import com.example.BankingAppCRUD.Application.DTOs.Requests.StatusChangeRequest;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.FundTransactionDTO;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Account.*;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Application.Exceptions.AccountNotFoundException;
import com.example.BankingAppCRUD.Application.Exceptions.InsufficientAmountException;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports.FundTransactionService;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import com.example.BankingAppCRUD.Infrastructure.Service.Account.CheckingAccountServiceImpl;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.CheckingAccountJPARepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checking-accounts")
@Tag(name = "Checking Account Management", description = "Checking account operations and transaction management")
@CrossOrigin(origins = "*")
public class CheckingAccountController {

    private static final Logger logger = LoggerFactory.getLogger(CheckingAccountController.class);

    private final CheckingAccountServiceImpl checkingAccountService;
    private final FundTransactionService transactionService;
    private final CheckingAccountJPARepository checkingAccountRepository;

    public CheckingAccountController(CheckingAccountServiceImpl checkingAccountService,
                                   FundTransactionService transactionService,
                                   CheckingAccountJPARepository checkingAccountRepository) {
        this.checkingAccountService = checkingAccountService;
        this.transactionService = transactionService;
        this.checkingAccountRepository = checkingAccountRepository;
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get checking account details", description = "Retrieve detailed information about a specific checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getAccountDetails(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting checking account details for accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            CheckingAccount account = checkingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Checking account not found with ID: " + accountId));

            AccountDTO accountResponse = AccountDTO.builder()
                    .account_Status(account.getAccount_status())
                    .account_info(account.getInfo())
                    .created_at(account.getCreatedAt())
                    .rate(account.getRate())
                    .accountType("CHECKING")
                    .build();

            logger.info("Successfully retrieved checking account details for accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("Account details retrieved successfully")
                    .build());

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error getting checking account details for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get checking account balance", description = "Retrieve the current balance of a specific checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getAccountBalance(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting balance for checking accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            Response response = checkingAccountService.viewBalance(accountId);
            logger.info("Successfully retrieved balance for checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting balance for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{accountId}/deposit")
    @Operation(summary = "Deposit money to checking account", description = "Deposit money into a specific checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money deposited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> depositMoney(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestBody DepositRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Processing deposit for checking accountId: {}, amount: {} [correlationId: {}]",
                accountId, request.getAmount(), correlationId);

        try {
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Response.builder().responseCode("400").message("Deposit amount must be greater than zero").build());
            }

            Response response = checkingAccountService.deposit(request.getAmount(), accountId);
            logger.info("Successfully processed deposit for checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found for deposit: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error processing deposit for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{accountId}/withdraw")
    @Operation(summary = "Withdraw money from checking account", description = "Withdraw money from a specific checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money withdrawn successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> withdrawMoney(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestBody WithdrawRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Processing withdrawal for checking accountId: {}, amount: {} [correlationId: {}]",
                accountId, request.getAmount(), correlationId);

        try {
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Response.builder().responseCode("400").message("Withdrawal amount must be greater than zero").build());
            }

            Response response = checkingAccountService.withdraw(request.getAmount(), accountId);
            logger.info("Successfully processed withdrawal for checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found for withdrawal: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (InsufficientAmountException e) {
            logger.warn("Insufficient funds for withdrawal from checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.badRequest()
                    .body(Response.builder().responseCode("400").message(e.getMessage()).build());
        } catch (Exception e) {
            logger.error("Error processing withdrawal for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{senderId}/transfer/{receiverId}")
    @Operation(summary = "Transfer money from checking account", description = "Transfer money between accounts from a checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money transferred successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> transferMoney(
            @Parameter(description = "Sender Account ID", required = true)
            @PathVariable UUID senderId,
            @Parameter(description = "Receiver Account ID", required = true)
            @PathVariable UUID receiverId,
            @RequestBody TransferRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Processing transfer from checking accountId: {} to accountId: {}, amount: {} [correlationId: {}]",
                senderId, receiverId, request.getAmount(), correlationId);

        try {
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Response.builder().responseCode("400").message("Transfer amount must be greater than zero").build());
            }

            Response response = checkingAccountService.transfer(request.getAmount(), receiverId, senderId);
            logger.info("Successfully processed transfer from checking account {} to {} [correlationId: {}]", senderId, receiverId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found for transfer [correlationId: {}]: {}", correlationId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (InsufficientAmountException e) {
            logger.warn("Insufficient funds for transfer [correlationId: {}]: {}", correlationId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Response.builder().responseCode("400").message(e.getMessage()).build());
        } catch (Exception e) {
            logger.error("Error processing transfer from checking account {} to {} [correlationId: {}]", senderId, receiverId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/transactions")
    @Operation(summary = "Get checking account transaction history", description = "Retrieve transaction history for a specific checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FundTransactionDTO>> getTransactionHistory(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting transaction history for checking accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            checkingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Checking account not found with ID: " + accountId));

            List<FundTransactionDTO> transactions = transactionService.getListTransactionHistoryById(accountId);

            logger.info("Successfully retrieved {} transactions for checking accountId: {} [correlationId: {}]",
                    transactions.size(), accountId, correlationId);
            return ResponseEntity.ok(transactions);

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting transaction history for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{accountId}/change-status")
    @Operation(summary = "Change checking account status", description = "Change the status of a specific checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account status changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> changeAccountStatus(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestBody StatusChangeRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Changing status for checking accountId: {} to {} [correlationId: {}]",
                accountId, request.getNewStatus(), correlationId);

        try {
            Response response = checkingAccountService.updateAccountStatus(request.getNewStatus(), accountId);
            logger.info("Successfully changed status for checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error changing status for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/interest-rate")
    @Operation(summary = "Get interest rate", description = "Get the current interest rate for a specific checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interest rate retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getInterestRate(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting interest rate for checking accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            CheckingAccount account = checkingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Checking account not found with ID: " + accountId));

            Rate rate = account.getRate();
            logger.info("Successfully retrieved rate for checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("Current interest rate: " + rate.getRateInfo() + "%")
                    .build());

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error getting rate for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{accountId}/set-rate")
    @Operation(summary = "Set interest rate", description = "Set the interest rate for a specific checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interest rate set successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> setInterestRate(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestBody RateRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Setting interest rate for checking accountId: {} to {} [correlationId: {}]",
                accountId, request.getRate(), correlationId);

        try {
            if (request.getRate() < 0) {
                return ResponseEntity.badRequest()
                        .body(Response.builder().responseCode("400").message("Interest rate cannot be negative").build());
            }

            Response response = checkingAccountService.setRate(accountId, request.getRate());
            logger.info("Successfully set rate for checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error setting rate for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/daily-transaction-limit")
    @Operation(summary = "Get daily transaction limit", description = "Get the daily transaction limit for a checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daily transaction limit retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getDailyTransactionLimit(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting daily transaction limit for checking accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            CheckingAccount account = checkingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Checking account not found with ID: " + accountId));

            Money dailyLimit = account.getDailyTransactionLimit();
            logger.info("Successfully retrieved daily transaction limit for checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("Daily transaction limit: " + dailyLimit.getAmount() + " " + dailyLimit.getCurrency())
                    .build());

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error getting daily transaction limit for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/overdraft-limit")
    @Operation(summary = "Get overdraft limit", description = "Get the overdraft limit for a checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdraft limit retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getOverdraftLimit(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting overdraft limit for checking accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            CheckingAccount account = checkingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Checking account not found with ID: " + accountId));

            Money overdraftLimit = account.getOverDraftLimit();
            logger.info("Successfully retrieved overdraft limit for checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("Overdraft limit: " + overdraftLimit.getAmount() + " " + overdraftLimit.getCurrency())
                    .build());

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error getting overdraft limit for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/monthly-fee")
    @Operation(summary = "Get monthly fee", description = "Get the monthly fee for a checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Monthly fee retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getMonthlyFee(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting monthly fee for checking accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            CheckingAccount account = checkingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Checking account not found with ID: " + accountId));

            Money monthlyFee = account.getMonthlyFee();
            logger.info("Successfully retrieved monthly fee for checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("Monthly fee: " + monthlyFee.getAmount() + " " + monthlyFee.getCurrency())
                    .build());

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error getting monthly fee for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{accountId}/apply-interest")
    @Operation(summary = "Apply interest", description = "Apply interest to a checking account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interest applied successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> applyInterest(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Applying interest for checking accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            Response response = checkingAccountService.applyRate(accountId);
            logger.info("Successfully applied interest for checking accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Checking account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error applying interest for checking accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }


}