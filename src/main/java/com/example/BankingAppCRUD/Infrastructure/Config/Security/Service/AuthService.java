package com.example.BankingAppCRUD.Infrastructure.Config.Security.Service;




import com.example.BankingAppCRUD.Application.DTOs.Requests.User.UserDTO;
import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.DTOs.UserResponseWithCredentials;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.DTOs.LoginDTO;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.DTOs.TokenDTO;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.User.AuthUser;
import com.example.BankingAppCRUD.Infrastructure.Service.User.UserServiceImpl;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthService {




    private final UserServiceImpl userServiceImpl;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;


    public AuthService (
            UserServiceImpl userServiceImpl,
            PasswordEncoder passwordEncoder,
            JWTService jwtService
    ) {
        this.jwtService = jwtService;
        this.userServiceImpl = userServiceImpl;
        this.passwordEncoder = passwordEncoder;

    }


    public TokenDTO login (LoginDTO loginDTO) {
        UserResponseWithCredentials userCredentials = userServiceImpl.getUserCredentialsByUsername(loginDTO.username());

        if (!passwordEncoder.matches(loginDTO.password(), userCredentials.passwordHash()))
                throw new ApplicationContextException("Password is incorrect");



        UserDTO userDTO = userCredentials.userDTO();

        AuthUser authUser = new AuthUser(userDTO.id(), userDTO.roles());

        String jwtToken = jwtService.createJwtToken(authUser);


        return new TokenDTO(jwtToken);
    }






}
