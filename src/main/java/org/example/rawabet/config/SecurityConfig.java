package org.example.rawabet.config;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origines autorisées (Angular dev + prod)
        config.setAllowedOrigins(List.of(
                "http://localhost:4200"
        ));

        // Méthodes HTTP autorisées
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Headers autorisés (Authorization pour JWT + Content-Type)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        // Expose Authorization dans la réponse (utile pour lire le token côté Angular)
        config.setExposedHeaders(List.of("Authorization"));

        // Autoriser l'envoi des cookies/credentials
        config.setAllowCredentials(true);

        // Durée du cache preflight (en secondes)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/users/add").permitAll()
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/users/me/**").authenticated()

                        // 🔐 ADMIN SYSTEM
                        .requestMatchers("/roles/create").hasAuthority("ADMIN_MANAGE")
                        .requestMatchers("/roles/delete/**").hasAuthority("ADMIN_MANAGE")
                        .requestMatchers("/users/add-with-role").hasAuthority("ADMIN_MANAGE")

                        // 🔐 MODULE
                        .requestMatchers("/cinema/**").hasAuthority("CINEMA_CREATE")
                        .requestMatchers("/event/**").hasAuthority("EVENT_CREATE")
                        .requestMatchers("/formation/**").hasAuthority("FORMATION_CREATE")
                        .requestMatchers("/carte/me").hasAuthority("FIDELITY_READ")
                        .requestMatchers("/carte/admin/**").hasAuthority("FIDELITY_UPDATE")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}