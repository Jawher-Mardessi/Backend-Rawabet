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

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ✅ CORS géré par Spring Security directement
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:4200"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/users/add").permitAll()
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/users/me/**").authenticated()
                        .requestMatchers("/roles/create").hasAuthority("ADMIN_MANAGE")
                        .requestMatchers("/roles/delete/**").hasAuthority("ADMIN_MANAGE")
                        .requestMatchers("/users/add-with-role").hasAuthority("ADMIN_MANAGE")
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