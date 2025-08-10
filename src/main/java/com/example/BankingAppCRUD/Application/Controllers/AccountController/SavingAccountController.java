package com.example.BankingAppCRUD.Application.Controllers.AccountController;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Account.AccountDTO;
import com.example.BankingAppCRUD.Application.DTOs.Requests.StatusChangeRequest;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.FundTransactionDTO;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Account.*;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Application.Exceptions.AccountNotFoundException;
import com.example.BankingAppCRUD.Application.Exceptions.InsufficientAmountException;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports.FundTransactionService;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;

import com.example.BankingAppCRUD.Infrastructure.Service.Account.SavingAccountServiceImpl;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.SavingAccountJPARepository;
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
@RequestMapping("/api/v1/saving-accounts")
@Tag(name = "Saving Account Management", description = "Saving account operations and interest management")
@CrossOrigin(origins = "*")
public class SavingAccountController {

    private static final Logger logger = LoggerFactory.getLogger(SavingAccountController.class);

    private final SavingAccountServiceImpl savingAccountService;
    private final FundTransactionService transactionService;
    private final SavingAccountJPARepository savingAccountRepository;

    public SavingAccountController(SavingAccountServiceImpl savingAccountService,
                                  FundTransactionService transactionService,
                                  SavingAccountJPARepository savingAccountRepository) {
        this.savingAccountService = savingAccountService;
        this.transactionService = transactionService;
        this.savingAccountRepository = savingAccountRepository;
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get saving account details", description = "Retrieve detailed information about a specific saving account")
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

        logger.info("Getting saving account details for accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            SavingAccount account = savingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Saving account not found with ID: " + accountId));

            AccountDTO accountResponse = AccountDTO.builder()
                    .account_Status(account.getAccount_status())
                    .account_info(account.getInfo())
                    .created_at(account.getCreatedAt())
                    .rate(account.getRate())
                    .accountType("SAVING")
                    .build();

            logger.info("Successfully retrieved saving account details for accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("Account details retrieved successfully")
                    .build());

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error getting saving account details for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get saving account balance", description = "Retrieve the current balance of a specific saving account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getAccountBalance(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting balance for saving accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            Response response = savingAccountService.viewBalance(accountId);
            logger.info("Successfully retrieved balance for saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting balance for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{accountId}/deposit")
    @Operation(summary = "Deposit money to saving account", description = "Deposit money into a specific saving account")
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

        logger.info("Processing deposit for saving accountId: {}, amount: {} [correlationId: {}]",
                accountId, request.getAmount(), correlationId);

        try {
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Response.builder().responseCode("400").message("Deposit amount must be greater than zero").build());
            }

            Response response = savingAccountService.deposit(request.getAmount(), accountId);
            logger.info("Successfully processed deposit for saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found for deposit: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error processing deposit for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{accountId}/withdraw")
    @Operation(summary = "Withdraw money from saving account", description = "Withdraw money from a specific saving account")
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

        logger.info("Processing withdrawal for saving accountId: {}, amount: {} [correlationId: {}]",
                accountId, request.getAmount(), correlationId);

        try {
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Response.builder().responseCode("400").message("Withdrawal amount must be greater than zero").build());
            }

            Response response = savingAccountService.withdraw(request.getAmount(), accountId);
            logger.info("Successfully processed withdrawal for saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found for withdrawal: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (InsufficientAmountException e) {
            logger.warn("Insufficient funds for withdrawal from saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.badRequest()
                    .body(Response.builder().responseCode("400").message(e.getMessage()).build());
        } catch (Exception e) {
            logger.error("Error processing withdrawal for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{senderId}/transfer/{receiverId}")
    @Operation(summary = "Transfer money from saving account", description = "Transfer money between accounts from a saving account")
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

        logger.info("Processing transfer from saving accountId: {} to accountId: {}, amount: {} [correlationId: {}]",
                senderId, receiverId, request.getAmount(), correlationId);

        try {
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Response.builder().responseCode("400").message("Transfer amount must be greater than zero").build());
            }

            Response response = savingAccountService.transfer(request.getAmount(), receiverId, senderId);
            logger.info("Successfully processed transfer from saving account {} to {} [correlationId: {}]", senderId, receiverId, correlationId);
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
            logger.error("Error processing transfer from saving account {} to {} [correlationId: {}]", senderId, receiverId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/transactions")
    @Operation(summary = "Get saving account transaction history", description = "Retrieve transaction history for a specific saving account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FundTransactionDTO>> getTransactionHistory(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting transaction history for saving accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            savingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Saving account not found with ID: " + accountId));

            List<FundTransactionDTO> transactions = transactionService.getListTransactionHistoryById(accountId);

            logger.info("Successfully retrieved {} transactions for saving accountId: {} [correlationId: {}]",
                    transactions.size(), accountId, correlationId);
            return ResponseEntity.ok(transactions);

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting transaction history for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{accountId}/change-status")
    @Operation(summary = "Change saving account status", description = "Change the status of a specific saving account")
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

        logger.info("Changing status for saving accountId: {} to {} [correlationId: {}]",
                accountId, request.getNewStatus(), correlationId);

        try {
            Response response = savingAccountService.updateAccountStatus(request.getNewStatus(), accountId);
            logger.info("Successfully changed status for saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error changing status for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/interest-rate")
    @Operation(summary = "Get interest rate", description = "Get the current interest rate for a specific saving account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interest rate retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getInterestRate(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting interest rate for saving accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            SavingAccount account = savingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Saving account not found with ID: " + accountId));

            Rate rate = account.getRate();
            logger.info("Successfully retrieved rate for saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("Current interest rate: " + rate.getRateInfo() + "%")
                    .build());

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error getting rate for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{accountId}/set-rate")
    @Operation(summary = "Set interest rate", description = "Set the interest rate for a specific saving account")
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

        logger.info("Setting interest rate for saving accountId: {} to {} [correlationId: {}]",
                accountId, request.getRate(), correlationId);

        try {
            if (request.getRate() < 0) {
                return ResponseEntity.badRequest()
                        .body(Response.builder().responseCode("400").message("Interest rate cannot be negative").build());
            }

            Response response = savingAccountService.setRate(accountId, request.getRate());
            logger.info("Successfully set rate for saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error setting rate for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/interest-accrued")
    @Operation(summary = "Get interest accrued", description = "Get the amount of interest accrued for a saving account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interest accrued retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getInterestAccrued(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting interest accrued for saving accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            SavingAccount account = savingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Saving account not found with ID: " + accountId));

            Money interestAccrued = account.getInterestAccrued();
            logger.info("Successfully retrieved interest accrued for saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("Interest accrued: " + interestAccrued.getAmount() + " " + interestAccrued.getCurrency())
                    .build());

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error getting interest accrued for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @GetMapping("/{accountId}/minimum-balance")
    @Operation(summary = "Get minimum balance", description = "Get the minimum balance required for a saving account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Minimum balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> getMinimumBalance(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting minimum balance for saving accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            SavingAccount account = savingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Saving account not found with ID: " + accountId));

            Money minimumBalance = account.getMinBalance();
            logger.info("Successfully retrieved minimum balance for saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(Response.builder()
                    .responseCode("200")
                    .message("Minimum balance: " + minimumBalance.getAmount() + " " + minimumBalance.getCurrency())
                    .build());

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error getting minimum balance for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{accountId}/apply-interest")
    @Operation(summary = "Apply interest", description = "Apply interest to a saving account based on its compound frequency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interest applied successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> applyInterest(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Applying interest for saving accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            Response response = savingAccountService.applyRate(accountId);
            logger.info("Successfully applied interest for saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error applying interest for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }

    @PostMapping("/{accountId}/update-compound-frequency")
    @Operation(summary = "Update compound frequency", description = "Update the compound frequency for interest calculation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compound frequency updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> updateCompoundFrequency(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestBody FrequencyRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Updating compound frequency for saving accountId: {} to {} [correlationId: {}]",
                accountId, request.getFrequency(), correlationId);

        try {
            Response response = savingAccountService.updateCompoundFrequency(request.getFrequency(), accountId);
            logger.info("Successfully updated compound frequency for saving accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Saving account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().responseCode("404").message("Account not found").build());
        } catch (Exception e) {
            logger.error("Error updating compound frequency for saving accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().responseCode("500").message("Internal server error").build());
        }
    }






}
