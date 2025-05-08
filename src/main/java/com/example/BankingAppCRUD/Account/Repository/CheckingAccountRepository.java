package com.example.BankingAppCRUD.Account.Repository;

import com.example.BankingAppCRUD.Account.Model.CheckingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface CheckingAccountRepository extends JpaRepository<CheckingAccount, Long> {




    @Modifying
    @Query(value = "UPDATE  _CheckingAccount SET balance = :Num WHERE id = :id",
            nativeQuery = true)
    int updateByBalance (@Param("Num") double Num , @Param("id") double id);






}
