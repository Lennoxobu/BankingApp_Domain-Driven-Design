package com.example.BankingAppCRUD.Infrastructure.Config.Security.Service;




import com.example.BankingAppCRUD.Domain.Entity.User.Ports.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    private final AuthUserCache authUserCache;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;


    public AuthService (
            AuthUserCache authUserCache,
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.authUserCache = authUserCache;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

    }


    public TokenDTO login (LoginDTO loginDTO) {
        userService.getU
    }

}
