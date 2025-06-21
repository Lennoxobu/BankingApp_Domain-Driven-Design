package com.example.BankingAppCRUD.Infrastructure.Repository.User;

import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


public interface UserJPARepository extends  JpaRepository<User, Long> {

    @Modifying

    @Query(value = "UPDATE _User SET firstName = :keyword WHERE id = :id",
            nativeQuery = true)
    int updateByFirstName (@Param("keyword") String value , @Param("id") long id );

    @Modifying
    @Query(value = "UPDATE _User SET lastName= :keyword WHERE id = :id",
            nativeQuery = true)
    int updateByLastName (@Param("keyword") String value, @Param("id") long id);


    @Modifying
    @Query(value="UPDATE _User SET checkingAccount= NULL WHERE id = :id",
                nativeQuery = true )
    int  removeCheckingAccount(@Param("id") Long id);


    @Modifying
    @Query(value = "UPDATE _User SET savingAccount= NULL WHERE id = :id" ,
                        nativeQuery = true)
    int removeSavingAccount (@Param("id") Long id);


}
