package com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports;

import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FundTransactionRepository<T extends FundTransaction>  {


    Optional<FundTransaction> findById (UUID transactionID );

    List<Optional<FundTransaction>> findAllById (UUID sender_receiverID);

    void save (T fundTransaction);

    T deleteTransaction (UUID id );

}
