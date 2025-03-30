package com.example.BankingAppCRUD.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public interface AccountService <T extends Account >  {

    public Page<T> getAllAccounts (Pageable pageable);

    public Optional<T> getAccountById (long ID) throws Exception;

    public T createAccount (AccountRequest account) throws Exception;

    public boolean deleteAccount (T account , Long ID );


    public double getRate (Long Id);

    public void setRate (Long Id, double value  );

    public int deposit (double value , Long id );

    public int withdraw (double value , Long id ) throws Exception;

    public int transfer (double value , Long receiverID,  Long grantorID ) throws Exception;

}
