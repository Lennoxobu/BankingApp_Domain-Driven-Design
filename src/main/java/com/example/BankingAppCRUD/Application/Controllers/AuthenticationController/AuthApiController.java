package com.example.BankingAppCRUD.Application.Controllers.AuthenticationController;

import com.example.BankingAppCRUD.Application.DTOs.Requests.User.UserDTO;
import com.example.BankingAppCRUD.Application.DTOs.Requests.User.UserRegistrationRequest;
import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.DTOs.LoginDTO;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.DTOs.TokenDTO;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {


    private final AuthService authService;




    @Autowired
    public AuthApiController (AuthService authService) {
        this.authService = authService;
    }


    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public TokenDTO login (@RequestBody LoginDTO loginDto) { return authService.login(loginDto); }












}
