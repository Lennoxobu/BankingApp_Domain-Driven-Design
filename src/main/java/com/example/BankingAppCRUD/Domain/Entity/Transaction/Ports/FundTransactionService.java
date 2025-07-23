package com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports;

import com.example.BankingAppCRUD.Application.DTOs.FundTransactionDTO;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;

public interface FundTransactionService {

     List<FundTransactionDTO> getListTransactionHistoryById (UUID id ) ;

     FundTransactionDTO getTransaction (UUID id );




     Response createTransaction (UUID  recieverId , UUID senderId ,long value ) ;

     Response createTransaction (UUID senderId , long value );





     Response deleteTransaction (UUID  id );

















}
