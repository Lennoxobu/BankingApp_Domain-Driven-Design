package com.example.BankingAppCRUD.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SavingAccountRepository extends JpaRepository<SavingAccount, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE _SavingAccount SET firstName = :keyword WHERE id = :id",
            nativeQuery = true)
    int updateByFirstName (@Param("keyword") String value , @Param("id") long id );

    @Modifying
    @Transactional
    @Query(value = "UPDATE _SavingAccount SET lastName= :keyword WHERE id = :id",
            nativeQuery = true)
    int updateByLastName (@Param("keyword") String value, @Param("id") long id);


    @Modifying
    @Transactional
    @Query(value = "UPDATE  _SavingAccount SET balance = :Num WHERE id = :id",
            nativeQuery = true)
    int updateByBalance (@Param("Num") double Num , @Param("id") double id);
}
