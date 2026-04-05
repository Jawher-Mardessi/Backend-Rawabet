package org.example.rawabet.config;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/users/add").permitAll()

                        // 🔐 ADMIN SYSTEM
                        .requestMatchers("/roles/create").hasAuthority("ADMIN_MANAGE")
                        .requestMatchers("/roles/delete/**").hasAuthority("ADMIN_MANAGE")
                        .requestMatchers("/users/add-with-role").hasAuthority("ADMIN_MANAGE")

                        // 🔐 MODULE
                        .requestMatchers("/cinema/**").hasAuthority("CINEMA_CREATE")
                        .requestMatchers("/event/**").hasAuthority("EVENT_CREATE")
                        .requestMatchers("/formation/**").hasAuthority("FORMATION_CREATE")
                        .requestMatchers("/carte/me").hasAuthority("FIDELITY_READ")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}