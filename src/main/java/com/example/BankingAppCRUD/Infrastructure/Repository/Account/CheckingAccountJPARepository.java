package com.example.BankingAppCRUD.Infrastructure.Repository.Account;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional
public interface CheckingAccountJPARepository extends JpaRepository<CheckingAccount, UUID> {


    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<CheckingAccount> findByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT a FROM Account a JOIN FETCH a.user WHERE a.accountId = :id")
    Optional<CheckingAccount> findByIdWithUser(@Param("id") UUID accountId);







}
