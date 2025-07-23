package com.example.BankingAppCRUD.Application.Mappers;

import com.example.BankingAppCRUD.Application.DTOs.AccountDTO;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class AccountMapper extends BaseMapper <Account, AccountDTO> {

    @Override
    public Account convertToEntity(AccountDTO dto, Object... args) {

        Account account;

        if (dto.accountType() == "Checking") {
            account = CheckingAccount.builder().build();
        } else if (dto.accountType() == "Saving" ) {
            account = SavingAccount.builder().build();
        } else { throw new Error ("Account type not Checking or Saving please check dto entry "); }

        if(!Objects.isNull(dto)){
                   BeanUtils.copyProperties(dto, account);
               }
               return account;

       }



    @Override
    public AccountDTO convertToDto(Account entity, Object... args) {

        AccountDTO accountDto = AccountDTO.builder().build();
        if(!Objects.isNull(entity)) {
            BeanUtils.copyProperties(entity, accountDto);
        }
        return accountDto;
    }
}
