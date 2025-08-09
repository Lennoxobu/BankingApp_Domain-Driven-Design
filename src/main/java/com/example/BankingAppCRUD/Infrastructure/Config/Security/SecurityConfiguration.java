package com.example.BankingAppCRUD.Infrastructure.Config.Security;


import com.example.BankingAppCRUD.Infrastructure.Config.Security.Exceptions.ApplicationAccessDeniedHandler;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.Exceptions.ApplicationAuthenticationEntryPoint;
import com.example.BankingAppCRUD.Infrastructure.Config.Security.Filters.SecurityAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    
    private final SecurityAuthenticationFilter securityAuthenticationFilter;
    private final ApplicationAuthenticationEntryPoint authenticationEntryPoint;
    private final ApplicationAccessDeniedHandler accessDeniedHandler;
    
    
    @Autowired
    SecurityConfiguration (
            SecurityAuthenticationFilter securityAuthenticationFilter,
            ApplicationAuthenticationEntryPoint authenticationEntryPoint,
            ApplicationAccessDeniedHandler accessDeniedHandler
    ) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.securityAuthenticationFilter = securityAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    
    
    @Bean
    public PasswordEncoder encoder () {
        return new BCryptPasswordEncoder();
    }
    
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       return http
               .addFilterBefore(securityAuthenticationFilter, AuthorizationFilter.class)
               .authorizeHttpRequests(
                       matcher ->
                               matcher
                                       .requestMatchers(
                                               "/swagger-ui.html",
                                               "/swagger-ui/*",
                                               "/v3/api-docs/**",
                                               "/v3/api-docs/swagger-config",
                                               "/actuator/health"
                                       )
                                       .permitAll())
               .authorizeHttpRequests(matcher ->
                                            matcher
                                                    //method security will be evaluted after DSL configs ,
                                                    //so we have to define public paths upfront
                                                    .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/users")
                                                    .permitAll())
               .authorizeHttpRequests(matcher -> matcher.anyRequest().authenticated())

                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                .frameOptions().deny()
                                .contentTypeOptions().and()
                                .httpStrictTransportSecurity(hsts -> hsts
                .maxAgeInSeconds(31536000)
                                        .includeSubDomains(true))
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
               .sessionManagement(
                       configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .exceptionHandling(
                       customizer ->
                                customizer
                                        .accessDeniedHandler(accessDeniedHandler)
                                        .authenticationEntryPoint(authenticationEntryPoint))

               .build();

    }
    //register NoOp authenticationManger to avoid log printed by default autoconfiguration
    @Bean
    public AuthenticationManager noOpAuthenticationManager () {return authentication -> null;}

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}



//.cors(cors -> cors.configurationSource(corsConfigurationSource()))
//        .csrf(csrf -> csrf.disable())
//        .headers(headers -> headers
//        .frameOptions().deny()
//                        .contentTypeOptions().and()
//                        .httpStrictTransportSecurity(hsts -> hsts
//        .maxAgeInSeconds(31536000)
//                                .includeSubdomains(true))
//        .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
//        .authorizeHttpRequests(auth -> auth
//        .requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        .anyRequest().authenticated())
//        .oauth2Login(Customizer.withDefaults())