package com.example.BankingAppCRUD.Infrastructure.Repository.User;


import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import com.example.BankingAppCRUD.Domain.Entity.User.Ports.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class PostgresUserRepository implements UserRepository<User> {

    private final UserJPARepository userJPARepository;

    @Autowired
    PostgresUserRepository (UserJPARepository userJPARepository) {
        this.userJPARepository = userJPARepository;
    }














}
