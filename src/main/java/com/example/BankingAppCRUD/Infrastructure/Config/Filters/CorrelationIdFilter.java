package com.example.BankingAppCRUD.Infrastructure.Config.Filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER_NAME ="X-Correlation-ID";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String correlationID = httpRequest.getHeader(CORRELATION_ID_HEADER_NAME);

        if (correlationID == null || correlationID.isEmpty()){
            correlationID = UUID.randomUUID().toString();

        }


        MDC.put(CORRELATION_ID_HEADER_NAME , correlationID);
        try {
            filterChain.doFilter(servletRequest , servletResponse );
        } finally {
            MDC.remove(CORRELATION_ID_HEADER_NAME);
        }
    }
}
