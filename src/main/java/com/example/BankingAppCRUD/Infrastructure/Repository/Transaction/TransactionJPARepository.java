package com.example.BankingAppCRUD.Infrastructure.Repository.Transaction;


import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


public interface TransactionJPARepository extends JpaRepository<FundTransaction, UUID> {



    List<FundTransaction> findFundTransferByFromAccount( UUID  accountId);

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccountId = :accountId OR t.destinationAccountId = :accountId ORDER BY t.timestamp DESC")
    List<FundTransaction> findBySourceAccountIdOrDestinationAccountId(@Param("accountId") UUID accountId);

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccountId = :accountId ORDER BY t.timestamp DESC")
    List<FundTransaction> findBySourceAccountId(@Param("accountId") UUID accountId);

    @Query("SELECT t FROM Transaction t WHERE t.destinationAccountId = :accountId ORDER BY t.timestamp DESC")
    List<FundTransaction> findByDestinationAccountId(@Param("accountId") UUID accountId);

}
