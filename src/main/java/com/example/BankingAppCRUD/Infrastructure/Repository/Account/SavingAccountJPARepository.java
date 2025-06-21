package com.example.BankingAppCRUD.Infrastructure.Repository.Account;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


public interface SavingAccountJPARepository extends JpaRepository<SavingAccount, UUID> {




    @Modifying
    @Query(value = "UPDATE  _SavingAccount SET balance = :Num WHERE id = :id",
            nativeQuery = true)
    int updateByBalance (@Param("Num") double Num , @Param("id") UUID id);
}
