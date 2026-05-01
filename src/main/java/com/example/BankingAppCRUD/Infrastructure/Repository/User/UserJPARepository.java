package com.example.BankingAppCRUD.Infrastructure.Repository.User;

import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface UserJPARepository extends  JpaRepository<User, UUID> {

    Optional<User> findByUsername (String username);
}
