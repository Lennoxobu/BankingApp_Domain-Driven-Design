package com.example.BankingAppCRUD.Infrastructure.Repository.Transaction;


import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


public interface TransactionJPARepository extends JpaRepository<FundTransaction, UUID> {


}
