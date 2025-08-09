package com.example.BankingAppCRUD.Infrastructure.Config.Security.Exceptions;

import com.example.BankingAppCRUD.Infrastructure.Config.Security.DTOs.ApiErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.io.IOException;


@Component
@Slf4j
public class ApplicationAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public ApplicationAuthenticationEntryPoint (ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void commence (
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        log.error("Authentication exception occured for request: {}" , reuqest , authException );

        ApiErrorResponse apiErrorResponse =  new ApitErrorResponse (authException.getMessage());



        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), apiErrorResponse);

    }
}
