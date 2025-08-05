package com.example.BankingAppCRUD.Infrastructure.Service.Transaction;

import com.example.BankingAppCRUD.Application.DTOs.FundTransactionDTO;
import com.example.BankingAppCRUD.Application.Mappers.TransactionMapper;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports.FundTransactionService;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.TransactionStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.TransactionType;
import com.example.BankingAppCRUD.Infrastructure.Repository.Transaction.TransactionJPARepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FundTransactionServiceImpl implements FundTransactionService {



    private final TransactionJPARepository fundTransactionRepository;
    private final TransactionMapper transactionMapper;
    private final FundTransactionDTO fundTransactionDTO;


    @Autowired
    public FundTransactionServiceImpl (TransactionJPARepository transactionRepository , TransactionMapper transactionMapper , FundTransactionDTO fundTransactionDTO) {
        this.fundTransactionRepository = transactionRepository;
        this.transactionMapper = new TransactionMapper();
        this.fundTransactionDTO = fundTransactionDTO;
    }


    @Override
    public List<FundTransactionDTO> getListTransactionHistoryById(UUID id) {

        return this.transactionMapper.convertToDtoList( fundTransactionRepository.findFundTransferByFromAccount(id));
    }

    @Override
    public FundTransactionDTO getTransaction(UUID id) {
        return fundTransactionRepository.findById(id).map(
                account -> this.transactionMapper.convertToDto(account)).orElse(FundTransactionDTO.builder().build()
        );
    }






    @Override
    public Response createTransaction(UUID recieverId, UUID senderId, long value) {

       if (value <= 0 )
            Response.builder().responseCode("500").message("Value must be greater than zero").build();


       FundTransactionDTO dto = FundTransactionDTO.builder().destinationAccountID(recieverId)
               .sourceAccountID(senderId)
               .status(TransactionStatus.STARTING_PENDING)
               .amount(
                       Money.builder().amount(value).currency("GBP").build()
               ).timeStamp(Date.from(Instant.now()))
               .type(TransactionType.TRANSFER)
               .build();


       FundTransaction transaction = this.transactionMapper.convertToEntity(dto);


        this.fundTransactionRepository.save(transaction);

        return  Response.builder().responseCode("200").message(transaction.getId().toString()).build();



    }

    @Override
    //Deposit / Withdraw
    public Response createTransaction(UUID senderId, long value , String type) {
        if (value <= 0 )
            Response.builder().responseCode("500").message("Value must be greater than zero").build();



        FundTransactionDTO dto = FundTransactionDTO.builder().destinationAccountID(null)
                .sourceAccountID(senderId)
                .status(TransactionStatus.STARTING_PENDING)
                .amount(
                        Money.builder().amount(value).currency("GBP").build()
                ).timeStamp(Date.from(Instant.now()))
                .type(TransactionType.valueOf(type.toUpperCase())).build();



        FundTransaction transaction = this.transactionMapper.convertToEntity(dto);


        this.fundTransactionRepository.save(transaction);

        return  Response.builder().responseCode("200").message(transaction.getId().toString()).build();
    }



    public  Response deleteTransaction (UUID id ) {

        this.fundTransactionRepository.deleteById(id);

        return Response.builder().responseCode("200").message("Success").build();


    }
}
