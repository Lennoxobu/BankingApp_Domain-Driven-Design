package com.example.BankingAppCRUD.Application.Controllers.AuthenticationController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {


    private final AuthService authService;



    public AuthApiController (AuthService authService) {
        this.authService = authService;
    }


    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public TokenDTO login (@RequestBody LoginDTO loginDto) { return authService.login(loginDto); }



    @PreAuthorize("isAuthenticated ()")
    @PostMapping("/logout")
    //Hard coded "Token" refer tom document to change it to dynamic : https://medium.com/@ihor.polataiko/spring-security-guide-part-2-authentication-with-opaque-token-0417187f7e9e
    @SecurityRequirement(name = "Token")
    public void logout (HttpServletRequest httpServletRequest) {
        String token =
                Optional.ofNullable(
                        httpServletRequest.getHeader(AuthConstants.AUTHORIZATION_HEADER)
                ).orElseThrow();

        authService.logout(token;
    }
}
