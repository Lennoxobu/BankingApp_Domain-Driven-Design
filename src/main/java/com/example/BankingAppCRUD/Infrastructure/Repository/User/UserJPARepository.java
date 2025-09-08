package com.example.BankingAppCRUD.Infrastructure.Repository.User;

import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


public interface UserJPARepository extends  JpaRepository<User, UUID> {

    Optional<User> findByUsername (String username);
}
