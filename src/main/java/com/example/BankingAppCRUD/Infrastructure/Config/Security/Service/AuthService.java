package com.example.BankingAppCRUD.Infrastructure.Config.Security.Service;




import com.example.BankingAppCRUD.Application.DTOs.UserDTO;
import com.example.BankingAppCRUD.Application.DTOs.UserResponseWithCredentials;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.Authetication.AuthUserCache;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.DTOs.LoginDTO;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.DTOs.TokenDTO;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.User.AuthUser;
import com.example.BankingAppCRUD.Infrastructure.Service.User.UserServiceImpl;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class AuthService {


    private final AuthUserCache authUserCache;

    private final UserServiceImpl userServiceImpl;

    private final PasswordEncoder passwordEncoder;


    public AuthService (
            AuthUserCache authUserCache,
            UserServiceImpl userServiceImpl,
            PasswordEncoder passwordEncoder
    ) {
        this.authUserCache = authUserCache;
        this.userServiceImpl = userServiceImpl;
        this.passwordEncoder = passwordEncoder;

    }


    public TokenDTO login (LoginDTO loginDTO) {
        UserResponseWithCredentials userCredentials = userServiceImpl.getUserCredentialsByUsername(loginDTO.username());

        if (!passwordEncoder.matches(loginDTO.password(), userCredentials.passwordHash()))
                throw new ApplicationContextException("Password is incorrect");

        String token = UUID.randomUUID().toString();

        UserDTO userDTO = userCredentials.userDTO();

        AuthUser authUser = new AuthUser(userDTO.id(), userDTO.roles());


        authUserCache.login(token , authUser);

        return new TokenDTO(token);
    }


    public void logout (String  token ) {
        authUserCache.logout(token);
    }

}
