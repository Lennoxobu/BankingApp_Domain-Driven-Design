package com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports;

import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.FundTransactionDTO;
import com.example.BankingAppCRUD.Application.Response.Response;

import java.util.List;
import java.util.UUID;

public interface FundTransactionService {

     List<FundTransactionDTO> getListTransactionHistoryById (UUID id ) ;

     FundTransactionDTO getTransaction (UUID id );




     Response createTransaction (UUID  recieverId , UUID senderId ,long value ) ;

     Response createTransaction (UUID senderId , long value , String type );





     Response deleteTransaction (UUID  id );

















}
