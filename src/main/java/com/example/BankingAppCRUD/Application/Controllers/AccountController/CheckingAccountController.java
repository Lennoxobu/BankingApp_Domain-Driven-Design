package com.example.BankingAppCRUD.Application.Controllers.AccountController;

import com.example.BankingAppCRUD.Application.DTOs.FundTransactionDTO;
import com.example.BankingAppCRUD.Application.DTOs.OperationalResultDTO;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports.FundTransactionService;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import com.example.BankingAppCRUD.Infrastructure.Repository.Account.CheckingAccountJPARepository;
import com.example.BankingAppCRUD.Infrastructure.Service.Account.CheckingAccountServiceImpl;
import com.example.BankingAppCRUD.Infrastructure.Service.Transaction.FundTransactionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Checking Account Management", description = "Banking account operations")
@CrossOrigin(origins = "*")
public class CheckingAccountController {

    private static final Logger logger = LoggerFactory.getLogger(CheckingAccountController.class);

    private final CheckingAccountServiceImpl checkingAccountService;

    private final FundTransactionService transactionService;
    private final CheckingAccountJPARepository checkingAccountRepository;

    public CheckingAccountController (CheckingAccountServiceImpl checkingAccountService,
                              FundTransactionServiceImpl transactionService, CheckingAccountJPARepository  checkingAccountRepository ) {
        this.checkingAccountService = checkingAccountService;
        this.transactionService = transactionService;
        this.checkingAccountRepository = checkingAccountRepository;
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account details", description = "Retrieve detailed information about a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Response> getAccountDetails(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting account details for accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            Account account = checkingAccountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

            AccountResponse accountResponse = mapToAccountResponse(account);

            logger.info("Successfully retrieved account details for accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(accountResponse);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting account details for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get account balance", description = "Retrieve the current balance of a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Response> getAccountBalance(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting balance for accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

            // Using Money value object directly - no primitive conversion
            BalanceResponse balanceResponse = new BalanceResponse(accountId, account.getAccountBalance());

            logger.info("Successfully retrieved balance for accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(balanceResponse);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting balance for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{accountId}/deposit")
    @Operation(summary = "Deposit money", description = "Deposit money into a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money deposited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Response> depositMoney(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @Valid @RequestBody DepositMoneyCommand command,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Processing deposit for accountId: {}, amount: {} [correlationId: {}]",
                accountId, command.getAmount(), correlationId);

        try {
            command.setAccountId(accountId);
            checkingAccountService.depositMoney(command);

            OperationalResultDto result = new OperationalResultDto();
            result.setSuccess(true);
            result.setMessage("Money deposited successfully");
            result.setTransactionId(UUID.randomUUID());

            logger.info("Successfully processed deposit for accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(result);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found for deposit: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid deposit request for accountId: {} [correlationId: {}]: {}", accountId, correlationId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResult(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error processing deposit for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResult("Internal server error"));
        }
    }

    @PostMapping("/{accountId}/withdraw")
    @Operation(summary = "Withdraw money", description = "Withdraw money from a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money withdrawn successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Response> withdrawMoney(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @Valid @RequestBody WithdrawMoneyCommand command,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Processing withdrawal for accountId: {}, amount: {} [correlationId: {}]",
                accountId, command.getAmount(), correlationId);

        try {
            command.setAccountId(accountId);
            checkingAccountService.withdrawMoney(command);

            OperationalResultDTO result = new OperationalResultDTO();

            result.setSuccess(true);
            result.setMessage("Money withdrawn successfully");
            result.setTransactionId(UUID.randomUUID());

            logger.info("Successfully processed withdrawal for accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(result);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found for withdrawal: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid withdrawal request for accountId: {} [correlationId: {}]: {}", accountId, correlationId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResult(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error processing withdrawal for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResult("Internal server error"));
        }
    }

    @PostMapping("/{senderId}/transfer/{receiverId}")
    @Operation(summary = "Transfer money", description = "Transfer money between two accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money transferred successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Response> transferMoney(
            @Parameter(description = "Sender Account ID", required = true)
            @PathVariable UUID senderId,
            @Parameter(description = "Receiver Account ID", required = true)
            @PathVariable UUID receiverId,
            @Valid @RequestBody TransferMoneyCommand command,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Processing transfer from accountId: {} to accountId: {}, amount: {} [correlationId: {}]",
                senderId, receiverId, command.getAmount(), correlationId);

        try {
            command.setFromAccountId(senderId);
            command.setToAccountId(receiverId);
            checkingAccountService.transferMoney(command);

            OperationalResultDTO result = new OperationalResultDTO();
            result.setSuccess(true);
            result.setMessage("Money transferred successfully");
            result.setTransactionId(UUID.randomUUID());

            logger.info("Successfully processed transfer from {} to {} [correlationId: {}]", senderId, receiverId, correlationId);
            return ResponseEntity.ok(result);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found for transfer [correlationId: {}]: {}", correlationId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid transfer request [correlationId: {}]: {}", correlationId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResult(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error processing transfer from {} to {} [correlationId: {}]", senderId, receiverId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResult("Internal server error"));
        }
    }

    @GetMapping("/{accountId}/transactions")
    @Operation(summary = "Get transaction history", description = "Retrieve transaction history for a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<List<FundTransactionDTO>> getTransactionHistory(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting transaction history for accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            // Verify account exists
            accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

            List<FundTransactionDTO> transactions = transactionService.getTransactionHistory(accountId);
            List<TransactionResponse> transactionResponses = transactions.stream()
                    .map(this::mapToTransactionResponse)
                    .collect(Collectors.toList());

            logger.info("Successfully retrieved {} transactions for accountId: {} [correlationId: {}]",
                    transactions.size(), accountId, correlationId);
            return ResponseEntity.ok(transactionResponses);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting transaction history for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{accountId}/change-status")
    @Operation(summary = "Change account status", description = "Change the status of a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account status changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Response> changeAccountStatus(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @Valid @RequestBody ChangeAccountStatusCommand command,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Changing status for accountId: {} to {} [correlationId: {}]",
                accountId, command.getNewStatus(), correlationId);

        try {
            command.setAccountId(accountId);
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

            if (account instanceof CheckingAccount) {
                checkingAccountService.updateAccountStatus(accountId, command.getNewStatus());
            } else if (account instanceof SavingAccount) {
                savingAccountService.updateAccountStatus(accountId, command.getNewStatus());
            }

            OperationResultDto result = new OperationResultDto();
            result.setSuccess(true);
            result.setMessage("Account status changed successfully");

            logger.info("Successfully changed status for accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(result);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error changing status for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResult("Internal server error"));
        }
    }

    @GetMapping("/{accountId}/get-rate")
    @Operation(summary = "Get interest rate", description = "Get the current interest rate for a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interest rate retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Rate> getInterestRate(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting interest rate for accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            // Return Rate value object directly - no DTO conversion needed
            Rate rate = checkingAccountService.getRate(accountId);

            logger.info("Successfully retrieved rate for accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(rate);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting rate for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{accountId}/get-interest-accrued")
    @Operation(summary = "Get interest accrued", description = "Get the amount of interest accrued for a savings account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interest accrued retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "Account is not a savings account")
    })
    public ResponseEntity<InterestAccruedResponse> getInterestAccrued(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting interest accrued for accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

            if (!(account instanceof SavingAccount)) {
                return ResponseEntity.badRequest().build();
            }

            SavingAccount savingAccount = (SavingAccount) account;
            // Using Money value object directly
            Money interestAccrued = savingAccount.getInterestAccrued();

            InterestAccruedResponse response = new InterestAccruedResponse(accountId, interestAccrued);

            logger.info("Successfully retrieved interest accrued for accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting interest accrued for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{accountId}/get-min-balance")
    @Operation(summary = "Get minimum balance", description = "Get the minimum balance required for a savings account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Minimum balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "Account is not a savings account")
    })
    public ResponseEntity<MinimumBalanceResponse> getMinimumBalance(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        logger.info("Getting minimum balance for accountId: {} [correlationId: {}]", accountId, correlationId);

        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

            if (!(account instanceof SavingAccount)) {
                return ResponseEntity.badRequest().build();
            }

            SavingAccount savingAccount = (SavingAccount) account;
            // Using Money value object directly
            Money minimumBalance = savingAccount.getMinimumBalance();

            MinimumBalanceResponse response = new MinimumBalanceResponse(accountId, minimumBalance);

            logger.info("Successfully retrieved minimum balance for accountId: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.ok(response);

        } catch (AccountNotFoundException e) {
            logger.warn("Account not found: {} [correlationId: {}]", accountId, correlationId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting minimum balance for accountId: {} [correlationId: {}]", accountId, correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}