package com.example.BankingAppCRUD.Infrastructure.Config.Security.Authetication;

import com.example.BankingAppCRUD.Domain.ValueObject.Role;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.User.AuthUser;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;



@AllArgsConstructor
public class AuthenticationImpl implements Authentication {


    private AuthUser authUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities () {
            return authUser.roles().stream()
                    .map(Role :: name)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
    }



    @Override
    public Object getDetails () {
        return null;
    }


    @Override
    public Object getCredentials () {
        return null;
    }

    @Override
    public Object getPrincipal () {
        return authUser;
    }



    @Override
    public boolean isAuthenticated () {
        return true;
    }


    @Override
    public void setAuthenticated (boolean isAuthenticated ) throws IllegalArgumentException {
        //NoOp
    }


    @Override
    public String getName () {
        return null;

    }



    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults () {
        return new GrantedAuthorityDefaults("");
    }



}
