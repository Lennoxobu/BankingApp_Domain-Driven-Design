package com.example.BankingAppCRUD.Infrastructure.Config.InterestRate;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;
import static java.util.Arrays.stream;


@Service
public class InterestRateService {

    private final WebClient webClient;
    private final ObjectMapper mapper;

    public InterestRateService(WebClient.Builder webClientBuilder, ObjectMapper mapper) {
        this.webClient = webClientBuilder.baseUrl("https://api.api-ninjas.com/v1").build();
        this.mapper = mapper;
    }

    public Mono<Double> getInterestRate() {

        return webClient.get()
                .uri("/interestrate?rate=central_bank_gb")
                .header("X-Api-Key", BankRateAPIKey.UK_KEY.toString())// Use "X-Api-Key" if required
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CentralBankRate.class)  // Convert JSON to Java object
                .flatMap(response -> {
                    if (response.getCentralBankRates().isEmpty()) {
                        return Mono.empty();  // If no rates are available, return empty Mono
                    }
                    Double bankRate = response.getCentralBankRates()
                            .stream()
                            .filter(rate -> rate.getCountry().equals("United_Kingdom"))
                            .findFirst()
                            .map(CountryBankRate::getRatePct)
                            .orElse(0.0);

                    return Mono.just(bankRate);

                })
                .defaultIfEmpty(0.0) // Default value if API returns an empty list
                .onErrorResume(WebClientResponseException.class, ex -> {
                    System.err.println("API error: " + ex.getResponseBodyAsString());
                    return Mono.just(0.0); // Fallback to 0.0 in case of an error
                })
                .onErrorResume(Exception.class, ex -> {
                    System.err.println("Unexpected error: " + ex.getMessage());
                    return Mono.just(0.0);
                });

    }


}


