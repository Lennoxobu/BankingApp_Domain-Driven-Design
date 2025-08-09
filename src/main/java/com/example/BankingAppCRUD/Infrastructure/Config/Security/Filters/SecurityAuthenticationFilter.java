package com.example.BankingAppCRUD.Infrastructure.Config.Security.Filters;


import com.example.BankingAppCRUD.Infrastructure.Config.Security.Exceptions.TokenAuthticationException;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.Authetication.AuthUserCache;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.Authetication.AuthenticationImpl;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.User.AuthUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component

public class SecurityAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUserCache authUserCache;


    public SecurityAuthenticationFilter (AuthUserCache authUserCache) {
        this.authUserCache = authUserCache;
    }

    @Override
    protected void doFilterInternal (
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authenticationHeader = request.getHeader("AUTHORIZATION");




        if (authenticationHeader == null ) {
            //Authentication token is not present , let's rely on anonymous authentication

            filterChain.doFilter(request, response);
            return;
        }


        AuthUser authUser = authUserCache.getByToken(authenticationHeader)
                .orElseThrow(() -> new TokenAuthticationException("Token is not valid"));



        AuthenticationImpl authentication =  new AuthenticationImpl(authUser);


        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);


        filterChain.doFilter(request , response);

    }
}
