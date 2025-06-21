package com.example.BankingAppCRUD.Infrastructure.Repository.Account;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


public interface CheckingAccountJPARepository extends JpaRepository<CheckingAccount, UUID> {




    @Modifying
    @Query(value = "UPDATE  _CheckingAccount SET balance = :Num WHERE id = :id",
            nativeQuery = true)
    int updateByBalance (@Param("Num") double Num , @Param("id") UUID id);






}
