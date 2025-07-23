package com.example.BankingAppCRUD.Application.Mappers;

import com.example.BankingAppCRUD.Application.DTOs.AccountDTO;
import com.example.BankingAppCRUD.Application.DTOs.FundTransactionDTO;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports.FundTransactionService;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class TransactionMapper extends BaseMapper<FundTransaction, FundTransactionDTO> {

    @Override
    public FundTransaction convertToEntity(FundTransactionDTO dto, Object... args) {

        FundTransaction transaction = FundTransaction.builder().build();

        if(!Objects.isNull(dto)){
            BeanUtils.copyProperties(dto, transaction);
        }
        return transaction;

    }



    @Override
    public FundTransactionDTO convertToDto(FundTransaction entity, Object... args) {

        FundTransactionDTO  dto  = FundTransactionDTO.builder().build();
        if(!Objects.isNull(entity)) {
            BeanUtils.copyProperties(entity, dto  );
        }
        return dto;
    }


}
