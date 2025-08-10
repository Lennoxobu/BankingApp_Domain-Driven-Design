package com.example.BankingAppCRUD.Infrastructure.Config.Security.Filters;


import com.example.BankingAppCRUD.Infrastructure.Config.Security.Exceptions.TokenAuthenticationException;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.Exceptions.TokenAuthticationException;

import com.example.BankingAppCRUD.Infrastructure.Config.Security.Authetication.AuthenticationImpl;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.Service.JWTService;
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

    private final JWTService  jwtService;


    public SecurityAuthenticationFilter ( JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal (
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authenticationHeader = request.getHeader("AUTHORIZATION");


        if (authenticationHeader == null) {
            //Authentication token is not present , let's rely on anonymous authentication

            filterChain.doFilter(request, response);
            return;
        }


        String jwtToken = stripBearerPrefix(authenticationHeader);


        AuthUser authUser;
        try {
            authUser = jwtService.resolveJwtToken(jwtToken);
        } catch (TokenAuthenticationException ex) {
            throw new ServletException("Issue found with Jwt token ");
        }


        AuthenticationImpl authentication = new AuthenticationImpl(authUser);


        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);


        filterChain.doFilter(request, response);

    }


    //Helper method to takes bearer prefix of the JWT token
    String stripBearerPrefix (String token ) {
        if (!token.startsWith("Bearer")) {
            throw new TokenAuthticationException("Unsupported authentication scheme");
        }

        return token.substring(7);
    }
}
