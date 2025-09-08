package com.example.BankingAppCRUD.Application.Controllers.TransactionController;



import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.FundTransactionDTO;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.TransactionFilterRequest;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.TransactionPaginationRequest;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.TransactionSummaryRequest;
import com.example.BankingAppCRUD.Application.Exceptions.TransactionNotFoundException;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Infrastructure.Service.Transaction.FundTransactionServiceImpl;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction service ", description = "Transaction service for AI service layer ")
@CrossOrigin(origins = "*")
public class TransactionController {


    private final Logger logger =  LoggerFactory.getLogger(TransactionController.class);
    private final Gson  jsonMapper  = new Gson();
    private final FundTransactionServiceImpl fundTransactionService;

    @Autowired
    TransactionController (FundTransactionServiceImpl fundTransactionService ) {
        this.fundTransactionService =  fundTransactionService;
    }

    @GetMapping("/{account_id}/transactions")
    @Operation(summary = "Transactions for api service layer", description = "Retrieve detailed layout  of transactions for API service layer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions acquired successfully"),
            @ApiResponse(responseCode = "404", description = "Transactions not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<FundTransactionDTO>> getTransactionHistory(    @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                              @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                              @RequestParam(name = "sort", defaultValue = "[{\"field\":\"date\",\"direction\":\"desc\"}]") String sort,
                                                                              @RequestParam(name = "date", required = false) Timestamp date,
                                                                              @RequestParam(name = "amount", required = false) Long amount,
                                                                              @Parameter(description = "Account ID", required = true)
                                                                              @RequestHeader(value = "X-Correlation-ID", required = false)
                                                                              String correlationId ,
                                                                              @PathVariable UUID account_id ) {

        logger.info("Getting transaction history account_id: {} [correlationId: {} ", account_id ,correlationId );

        try {

            Page<FundTransactionDTO> transactionDto =  fundTransactionService.searchTransactionWithPaginationSortingFiltering(
                    TransactionPaginationRequest.builder()
                            .sourceAccountID(account_id)
                            .destinationAccountID(account_id)
                            .timeStamp(date)
                            .amount(Money.builder()
                                    .amount(amount).build())
                            .page(page)
                            .size(size)
                            .sort(sort)
                            .build()
            );


            return ResponseEntity.ok(transactionDto);


        } catch (RuntimeException ex ) {
            logger.warn("account_id: {},[correlation_id: {}] ", account_id, correlationId);
            return ResponseEntity.badRequest().build();
        }




    }



    @GetMapping("/{accountId}/get-transaction/{transaction_id}")
    @Operation(summary = "A Transaction for api service layer", description = "Retrieve detailed layout  of a single selected transaction  for API service layer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction acquired successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<FundTransactionDTO> getTransaction (      @Parameter(description = "Transaction ID", required = true)
                                                                    @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
                                                                    @PathVariable UUID transaction_id ) {

        try {
            logger.info("Get single transaction via its transaction_id: {}[correlation_id: {}] ", transaction_id,  correlationId);
            FundTransactionDTO transactionDTO =  fundTransactionService.getTransaction(transaction_id);

            return ResponseEntity.ok(transactionDTO);

        } catch (RuntimeException ex ) {

            logger.warn("Error place holder transaction_id : {} [correlation_id: {}]", transaction_id, correlationId);
            return ResponseEntity.badRequest().build();
        }

    }


    @DeleteMapping("/{account_id}/delete-transaction/{transaction_id}")
    @Operation(summary = "Delete transaction", description = "Delete a transactions for API service layer to be used for Admin role access ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions acquired successfully"),
            @ApiResponse(responseCode = "404", description = "Transactions not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteTransaction (       @Parameter(description = "Account ID", required = true)
                                                              @PathVariable UUID account_id,
                                                              @Parameter(description = "Transaction ID ", required = true)
                                                              @RequestHeader(value = "X-Correlation-ID", required = false)
                                                              String correlationId ,
                                                              @PathVariable  UUID transaction_id  ) {
            return null;



    }



    @DeleteMapping("/{account_id}/delete-transactions-list/")
    @Operation(summary = "Delete a list for transactions", description = "Deletes a list of transactions selected by the user , the user must be admin  set for AI layer ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions acquired successfully"),
            @ApiResponse(responseCode = "404", description = "Transactions not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteTransactionsList ( @Parameter(description = "Account ID", required = true)
                                                         @PathVariable UUID account_id,
                                                         @Parameter(description = "Transaction ID ", required = true)
                                                         @RequestHeader(value = "X-Correlation-ID", required = false)
                                                         String correlationId,
                                                         List<UUID> transaction_list
                                                             ) {


        try {

            logger.info("Deletes in bulk list of UUID  accounts account_id: {} [correlation_id: {}]" ,  account_id , correlationId);


            Response response = fundTransactionService.deleteTransactionsByList(account_id , transaction_list);
            return ResponseEntity.ok(response);


        } catch (RuntimeException ex ) {
            throw new TransactionNotFoundException("Transactions list not  selected ");


        }

    }



    @GetMapping("/{account_id}/transaction-search/")
    @Operation(summary = "Delete a list for transactions", description = "Deletes a list of transactions selected by the user , the user must be admin  set for AI layer ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions acquired successfully"),
            @ApiResponse(responseCode = "404", description = "Transactions not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<Page<TransactionFilterRequest>>  getTransactionBySearch (@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                                   @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                                   @RequestParam(name = "sort", defaultValue = "[{\"field\":\"date\",\"direction\":\"desc\"}]") String sort,
                                                                                   @RequestParam(name = "date", required = false) Timestamp date,
                                                                                   @RequestParam(name = "amount", required = false) Long amount,
                                                                                   @Parameter(description = "Account ID", required = true)
                                                                                   @RequestHeader(value = "X-Correlation-ID", required = false)
                                                                                   String correlationId,
                                                                                   @PathVariable UUID account_id
                                                                                ) {
        return null;
    }




    @GetMapping("/export-transaction-report/{account_id}")
    @Operation(summary = "Export Transaction report to AI layer ", description = "Provides export function in JSON for AI layer ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions acquired successfully"),
            @ApiResponse(responseCode = "404", description = "Transactions not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TransactionFilterRequest>> exportTransactionsReport (@Parameter(description = "Account ID", required = true)
                                                                                 @RequestHeader(value = "X-Correlation-ID", required = false)
                                                                                  String correlationId, @PathVariable UUID account_id) {

        try {
            // Not implemented yet




             return null ;
        } catch (RuntimeException ex ) {
            logger.warn("Holding Error account_id: {} [correlation_id: {}]", account_id,  correlationId);
            return ResponseEntity.badRequest().build();
        }

    }




    @GetMapping("/{account_id}/transaction-summary/")
    @Operation(summary = "Delete a list for transactions", description = "Deletes a list of transactions selected by the user , the user must be admin  set for AI layer ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions acquired successfully"),
            @ApiResponse(responseCode = "404", description = "Transactions not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<TransactionSummaryRequest> getTransactionSummary (@Parameter(description = "Account ID", required = true)
                                                                        @RequestHeader(value = "X-Correlation-ID", required = false)
                                                                        String correlationId,
                                                                        @PathVariable UUID account_id) {
        return null;
    }
























}
