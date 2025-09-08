package com.example.BankingAppCRUD.Infrastructure.Service.Transaction;

import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.FundTransactionDTO;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.TransactionFilterRequest;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.TransactionPaginationRequest;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.TransactionSortRequest;
import com.example.BankingAppCRUD.Application.Exceptions.TransactionNotFoundException;
import com.example.BankingAppCRUD.Application.Mappers.TransactionMapper;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports.FundTransactionService;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.TransactionStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.TransactionType;
import com.example.BankingAppCRUD.Infrastructure.Repository.Transaction.TransactionJPARepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;


@Service
@Transactional
public class FundTransactionServiceImpl implements FundTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(FundTransactionServiceImpl.class);

    private final TransactionJPARepository fundTransactionRepository;
    private final TransactionMapper transactionMapper =  new TransactionMapper() ;
    private final  ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    public FundTransactionServiceImpl (TransactionJPARepository transactionRepository  ) {
        this.fundTransactionRepository = transactionRepository;
    }


    @Override
    public List<FundTransactionDTO> getListTransactionHistoryById(UUID account_id) {
        
        if (account_id == null) {
            logger.warn("getListTransactionHistoryById called with null account_id");
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        
        logger.debug("Retrieving transaction history for account: {}", account_id);
        
        try {
            List<FundTransaction> transactions = fundTransactionRepository.findFundTransferByReceiverID(account_id);
            transactions.addAll(fundTransactionRepository.findFundTransferBySenderID(account_id));

            List<FundTransactionDTO> result = this.transactionMapper.convertToDtoList(transactions);
            
            logger.info("Found {} transactions for account {}", result.size(), account_id);
            return result;
            
        } catch (RuntimeException e) {
            logger.error("Failed to retrieve transaction history for account {}: {}", account_id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transaction history", e);
        }
    }


    public Page<FundTransactionDTO>  searchTransactionWithPaginationSortingFiltering  (TransactionPaginationRequest dto ) {
        //Create filter DTO

        TransactionFilterRequest filterDto =  TransactionFilterRequest.builder()
                .date(dto.getTimeStamp())
                .receiver_id(dto.getDestinationAccountID())
                .sender_id(dto.getSourceAccountID())
                .amount(dto.getAmount().getAmount())
                .build();


        // Parse and create sort orders

        List<TransactionSortRequest> sortDtos = jsonStringToSortDto(dto.getSort());
        List<Sort.Order> orders  =  new ArrayList<>();

        if (sortDtos != null) {
            for (TransactionSortRequest sortDto : sortDtos ) {
                Sort.Direction direction = Objects.equals(sortDto.getDirection(), "desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, sortDto.getDate().toString()));
            }
        }

        //Create page request with sorting

        PageRequest pageRequest = PageRequest.of (
                dto.getPage(),
                dto.getSize(),
                Sort.by(orders)
        );

        // Apply specification and pagination

        Specification<FundTransaction> specification = TransactionSpecification.getSpecification(filterDto);
        Page<FundTransaction> transactions = fundTransactionRepository.findAll(specification, pageRequest);



        // Map to DTO and return

        return transactions.map (transaction -> transactionMapper.convertToDto(transaction));


    }

    // Helper method parsing JSON objects to DTOs
    private List<TransactionSortRequest> jsonStringToSortDto (String jsonString ) {
        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<TransactionSortRequest>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });
        } catch (Exception ex ) {
            logger.info("Exception: {}", ex);
            return null;
        }
    }


    @Override
    public FundTransactionDTO getTransaction(UUID transaction_id) {
        
        if (transaction_id == null) {
            logger.warn("getTransaction called with null transaction_id");
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        
        logger.debug("Retrieving transaction with ID: {}", transaction_id);
        
        try {
            Optional<FundTransaction> transactionOpt = fundTransactionRepository.findById(transaction_id);
            
            if (transactionOpt.isPresent()) {
                FundTransactionDTO result = this.transactionMapper.convertToDto(transactionOpt.get());
                logger.debug("Successfully retrieved transaction {}", transaction_id);
                return result;
            } else {
                logger.warn("Transaction not found with ID: {}", transaction_id);
                throw new TransactionNotFoundException("Transaction not found with ID: " + transaction_id);
            }
            
        } catch (TransactionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to retrieve transaction {}: {}", transaction_id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transaction", e);
        }
    }






    @Override
    public Response createTransaction(UUID receiverId, UUID senderId, long value) {
        
        if (receiverId == null) {
            logger.warn("createTransaction failed: receiver ID is null");
            throw new IllegalArgumentException("Receiver ID cannot be null");
        }
        
        if (senderId == null) {
            logger.warn("createTransaction failed: sender ID is null");
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        
        if (value <= 0) {
            logger.warn("createTransaction failed: invalid value {} for transfer from {} to {}", value, senderId, receiverId);
            return Response.builder().responseCode("400").message("Value must be greater than zero").build();
        }
        
        logger.info("Creating transfer transaction: {} -> {}, amount: {}", senderId, receiverId, value);
        
        try {
            FundTransactionDTO dto = FundTransactionDTO.builder()
                    .destinationAccountID(receiverId)
                    .sourceAccountID(senderId)
                    .status(TransactionStatus.STARTING_PENDING)
                    .amount(Money.builder().amount(value).currency("GBP").build())
                    .timeStamp(Date.from(Instant.now()))
                    .type(TransactionType.TRANSFER)
                    .build();

            FundTransaction transaction = this.transactionMapper.convertToEntity(dto);
            this.fundTransactionRepository.save(transaction);
            
            logger.info("Successfully created transfer transaction {} from {} to {}", transaction.getId(), senderId, receiverId);
            return Response.builder().responseCode("200").message(transaction.getId().toString()).build();
            
        } catch (Exception e) {
            logger.error("Failed to create transfer transaction from {} to {}: {}", senderId, receiverId, e.getMessage(), e);
            throw new RuntimeException("Failed to create transaction", e);
        }
    }

    @Override
    public Response createTransaction(UUID senderId, long value, String type) {
        
        if (senderId == null) {
            logger.warn("createTransaction failed: sender ID is null");
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        
        if (type == null || type.trim().isEmpty()) {
            logger.warn("createTransaction failed: transaction type is null or empty for sender {}", senderId);
            throw new IllegalArgumentException("Transaction type cannot be null or empty");
        }
        
        if (value <= 0) {
            logger.warn("createTransaction failed: invalid value {} for {} transaction from {}", value, type, senderId);
            return Response.builder().responseCode("400").message("Value must be greater than zero").build();
        }
        
        logger.info("Creating {} transaction for account {}, amount: {}", type.toUpperCase(), senderId, value);
        
        try {
            TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
            
            FundTransactionDTO dto = FundTransactionDTO.builder()
                    .destinationAccountID(null)
                    .sourceAccountID(senderId)
                    .status(TransactionStatus.STARTING_PENDING)
                    .amount(Money.builder().amount(value).currency("GBP").build())
                    .timeStamp(Date.from(Instant.now()))
                    .type(transactionType)
                    .build();

            FundTransaction transaction = this.transactionMapper.convertToEntity(dto);
            this.fundTransactionRepository.save(transaction);
            
            logger.info("Successfully created {} transaction {} for account {}", type.toUpperCase(), transaction.getId(), senderId);
            return Response.builder().responseCode("200").message(transaction.getId().toString()).build();
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid transaction type '{}' for account {}: {}", type, senderId, e.getMessage());
            throw new IllegalArgumentException("Invalid transaction type: " + type, e);
        } catch (Exception e) {
            logger.error("Failed to create {} transaction for account {}: {}", type, senderId, e.getMessage(), e);
            throw new RuntimeException("Failed to create transaction", e);
        }
    }



    @Transactional
    public Response deleteTransaction(UUID transaction_id) {
        
        if (transaction_id == null) {
            logger.warn("deleteTransaction failed: transaction ID is null");
            throw new TransactionNotFoundException("Transaction ID cannot be null");
        }
        
        logger.info("Deleting transaction with ID: {}", transaction_id);
        
        try {
            Optional<FundTransaction> existingTransaction = fundTransactionRepository.findById(transaction_id);
            
            if (!existingTransaction.isPresent()) {
                logger.warn("Attempted to delete non-existent transaction: {}", transaction_id);
                throw new TransactionNotFoundException("Transaction not found with ID: " + transaction_id);
            }
            
            this.fundTransactionRepository.deleteById(transaction_id);
            logger.info("Successfully deleted transaction: {}", transaction_id);
            
            return Response.builder().responseCode("200").message("Transaction deleted successfully").build();
            
        } catch (TransactionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to delete transaction {}: {}", transaction_id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete transaction", e);
        }
    }



    @Transactional
    public Response deleteTransactionsByList(UUID account_id, List<UUID> transactionList) {
        
        if (account_id  == null) {
            logger.warn("Delete transactions failed: account UUID is null");
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        
        if (transactionList == null || transactionList.isEmpty()) {
            logger.warn("Delete transactions failed: transaction list is null or empty for account {}", account_id);
            throw new IllegalArgumentException("Transaction list cannot be null or empty");
        }
        
        logger.info("Starting bulk deletion of {} transactions for account {}", transactionList.size(), account_id );
        
        try {
            List<UUID> processedIds = new ArrayList<>();
            
            transactionList.stream()
                .filter(Objects::nonNull)
                .forEach(id -> {
                    try {
                        fundTransactionRepository.deleteById(id);
                        processedIds.add(id);
                        logger.debug("Successfully deleted transaction {}", id);
                    } catch (Exception e) {
                        logger.error("Failed to delete transaction {} for account {}: {}", id, account_id, e.getMessage());
                        throw new TransactionNotFoundException("Failed to delete transaction: " + id);
                    }
                });
            
            logger.info("Successfully deleted {} transactions for account {}", processedIds.size(), account_id);
            
            return Response.builder()
                .responseCode("200")
                .message(String.format("Successfully deleted %d transactions", processedIds.size()))
                .build();
                
        } catch (TransactionNotFoundException e) {
            logger.error("Transaction deletion failed for account {}: {}", account_id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during bulk transaction deletion for account {}: {}", account_id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete transactions: " + e.getMessage(), e);
        }
    }
}
