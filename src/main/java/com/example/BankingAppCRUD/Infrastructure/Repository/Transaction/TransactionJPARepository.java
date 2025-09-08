package com.example.BankingAppCRUD.Infrastructure.Repository.Transaction;


import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.List;
import java.util.UUID;


public interface TransactionJPARepository extends JpaRepository<FundTransaction, UUID> , JpaSpecificationExecutor<FundTransaction> {



    List<FundTransaction> findFundTransferBySenderID( UUID accountId);
    List<FundTransaction> findFundTransferByReceiverID (UUID accountId);


    List<FundTransaction> findAll(Specification<FundTransaction> specification);
}
