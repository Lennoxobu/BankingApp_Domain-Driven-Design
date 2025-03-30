package com.example.BankingAppCRUD.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface CheckingAccountRepository extends JpaRepository<CheckingAccount , Long> {

    @Modifying
    @Query(value = "UPDATE _CheckingAccount SET firstName = :keyword WHERE id = :id",
            nativeQuery = true)
    int updateByFirstName (@Param("keyword") String value , @Param("id") long id );

    @Modifying
    @Query(value = "UPDATE _CheckingAccount SET lastName= :keyword WHERE id = :id",
                    nativeQuery = true)
    int updateByLastName (@Param("keyword") String value, @Param("id") long id);


    @Modifying
    @Query(value = "UPDATE  _CheckingAccount SET balance = :Num WHERE id = :id",
            nativeQuery = true)
    int updateByBalance (@Param("Num") double Num , @Param("id") double id);






}
