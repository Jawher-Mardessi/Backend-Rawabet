package org.example.rawabet.config;

import lombok.RequiredArgsConstructor;

import org.example.rawabet.services.AbonnementServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AbonnementServiceImpl abonnementService;

    @Bean
    CommandLineRunner init() {
        return args -> {
            abonnementService.initAbonnements();
        };
    }
}