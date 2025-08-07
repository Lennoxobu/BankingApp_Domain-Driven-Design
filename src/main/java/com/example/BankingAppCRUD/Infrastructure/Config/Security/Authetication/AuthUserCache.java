package com.example.BankingAppCRUD.Infrastructure.Config.Security.Authetication;


import com.example.BankingAppCRUD.Infrastructure.Config.Security.User.AuthUser;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthUserCache {


    private final Map<String, AuthUser> sessions = new ConcurrentHashMap<>();


    public void login (String token , AuthUser authUser) {
        sessions.put(token , authUser);
    }


    public void logout (String token) {
        sessions.remove(token);
    }


    public Optional<AuthUser> getByToken (String token) {
        return Optional.ofNullable(sessions.get(token));
    }


    @Bean
    public PasswordEncoder encoder () {
        return new BCryptPasswordEncoder();
    }



}
