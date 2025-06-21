package com.example.BankingAppCRUD.Infrastructure.Repository.Transaction;


import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Ports.FundTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class PostgresTransactionRepository implements FundTransactionRepository <FundTransaction> {

    private final TransactionJPARepository transactionJPARepository;


    @Autowired
    PostgresTransactionRepository (TransactionJPARepository transactionJPARepository) {
        this.transactionJPARepository = transactionJPARepository;
    }

    @Override
    public Optional<FundTransaction> findById(UUID transactionID) {
        return Optional.empty();
    }

    @Override
    public List<Optional<FundTransaction>> findAllById(UUID sender_receiverID) {
        return List.of();
    }

    @Override
    public void save(FundTransaction fundTransaction) {

    }

    @Override
    public FundTransaction deleteTransaction(UUID id) {
        return null;
    }
}
