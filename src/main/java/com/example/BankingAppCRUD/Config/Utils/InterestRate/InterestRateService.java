package com.example.BankingAppCRUD.Config.Utils.InterestRate;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


@Service
    public class InterestRateService {

        private final WebClient webClient;

        public InterestRateService(WebClient.Builder webClientBuilder) {
            this.webClient = webClientBuilder.baseUrl("https://api.api-ninjas.com/v1").build();
        }

        public Mono<Double> getInterestRate() {
            return webClient.get()
                    .uri("/interestrate?country=United Kingdom")
                    .header(HttpHeaders.AUTHORIZATION, String.valueOf(BankRateAPIKey.UK_KEY)) // Use "X-Api-Key" if required
                    .retrieve()
                    .bodyToMono(CentralBankRate.class)  // Convert JSON to Java object
                    .flatMap(response -> {
                        if (response.getRatePct() == 0 ) {
                            return Mono.empty();  // If no rates are available, return empty Mono
                        }
                        return Mono.just(response.getRatePct());
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


