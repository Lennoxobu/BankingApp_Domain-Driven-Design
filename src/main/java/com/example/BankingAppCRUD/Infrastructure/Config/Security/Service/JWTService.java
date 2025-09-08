package com.example.BankingAppCRUD.Infrastructure.Config.Security.Service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.BankingAppCRUD.Domain.ValueObject.Role;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.Exceptions.TokenAuthenticationException;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.User.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JWTService {

    private static final String ROLES_CLAIM = "roles";

    private final Algorithm signingAlgorithm;


    public JWTService (@Value("${banking.jwt.signing-secret}") String signingSecret ) {



        // This example uses a symmetric signature of the JWT token , but if you want the issuer and the
        // verifier of the JWT token to be different applications you may want to use an asymmetric
        // signature


        this.signingAlgorithm = Algorithm.HMAC256(signingSecret);
    }

    public AuthUser resolveJwtToken (String token) throws TokenAuthenticationException {
        try {
            JWTVerifier verifier = JWT.require(signingAlgorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);




            UUID userId = UUID.fromString(decodedJWT.getSubject());
            List<Role> roles = decodedJWT.getClaim(ROLES_CLAIM).asList(Role.class);



            return new AuthUser(userId, roles);

        } catch (JWTVerificationException ex) {throw new TokenAuthenticationException("JWT is not valid");}

    }

    public String createJwtToken (AuthUser authUser ) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillis = nowMillis + 3600000;
        Date exp = new Date(expMillis);


        List<String> roles = authUser.roles().stream().map(Role :: name ).toList();

        return JWT.create()
                .withSubject(authUser.authId().toString())
                .withClaim(ROLES_CLAIM, roles )
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(signingAlgorithm);
    }
}
