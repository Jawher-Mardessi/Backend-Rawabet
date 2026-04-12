package org.example.rawabet.config;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
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

                        // ── PUBLIC ─────────────────────────────────────────────────
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/users/add").permitAll()

                        // WebSocket — handshake HTTP permis, auth ensuite via intercepteur STOMP
                        .requestMatchers("/ws/**").permitAll()

                        // ── CHAT ────────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/chat/messages/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/chat/join/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/chat/active/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/chat/session/seance/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/chat/session/**").hasAuthority("CINEMA_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/chat/session/**").hasAuthority("CINEMA_CREATE")
                        .requestMatchers(HttpMethod.GET, "/chat/sessions").hasAuthority("CINEMA_CREATE")

                        // ── USERS ────────────────────────────────────────────────────
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/users/me/**").authenticated()

                        // ── ADMIN SYSTEM ──────────────────────────────────────────────
                        .requestMatchers("/roles/**").hasAuthority("ADMIN_MANAGE")
                        .requestMatchers("/permissions/**").hasAuthority("ADMIN_MANAGE")
                        .requestMatchers("/users/add-with-role").hasAuthority("ADMIN_MANAGE")
                        .requestMatchers("/users/update").hasAuthority("ADMIN_MANAGE")
                        .requestMatchers("/users/*/roles").hasAuthority("ADMIN_MANAGE")

                        // ── CINEMA / EVENT ─────────────────────────────────────────────
                        .requestMatchers("/cinema/**").hasAuthority("CINEMA_CREATE")
                        .requestMatchers("/event/**").hasAuthority("EVENT_CREATE")

                        // ── CLUB — public ──────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/club").permitAll()
                        .requestMatchers(HttpMethod.GET, "/club/events").permitAll()
                        .requestMatchers(HttpMethod.GET, "/club/events/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/club/members").permitAll()

                        // ── CLUB — membres authentifiés ────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/club/members/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/club/members/leave").authenticated()
                        .requestMatchers(HttpMethod.POST, "/club/requests").authenticated()
                        .requestMatchers(HttpMethod.POST, "/club/reservations").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/club/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/club/reservations/my").authenticated()

                        // ── CLUB — admin ───────────────────────────────────────────────
                        .requestMatchers(HttpMethod.PUT, "/club").hasAuthority("CLUB_MANAGE")
                        .requestMatchers(HttpMethod.GET, "/club/requests/pending").hasAuthority("CLUB_MANAGE")
                        .requestMatchers(HttpMethod.PUT, "/club/requests/**").hasAuthority("CLUB_MANAGE")
                        .requestMatchers(HttpMethod.POST, "/club/events").hasAuthority("CLUB_CREATE")

                        // ── FIDÉLITÉ ───────────────────────────────────────────────────
                        .requestMatchers("/carte/me").hasAuthority("FIDELITY_READ")
                        .requestMatchers("/carte/admin/**").hasAuthority("FIDELITY_UPDATE")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}