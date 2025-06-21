package com.example.BankingAppCRUD.Domain.Entity.Account.Ports;

import com.example.BankingAppCRUD.Application.Request.AccountRequest;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;


public interface AccountService <T extends Account>  {

    public Page<T> getAllAccounts (Pageable pageable);

    public Optional<T> getAccountById (UUID id ) throws Exception;

    public T createAccount (AccountRequest account) throws Exception;

    public boolean deleteAccount (T account , UUID id );


    public double getRate ( UUID id);

    public void setRate (UUID id, double value  );

    public int deposit (double value , UUID id );

    public int withdraw (double value , UUID id ) throws Exception;

    public int transfer (double value , UUID receiverID,  UUID grantorID ) throws Exception;

}
