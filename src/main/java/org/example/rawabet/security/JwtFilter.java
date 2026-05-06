package org.example.rawabet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService     jwtService;
    private final UserRepository userRepository;

    public JwtFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService     = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Routes publiques — pas de vérification JWT
        if (path.contains("/auth/login")
                || path.contains("/auth/forgot-password")
                || path.contains("/auth/reset-password")
                || path.contains("/auth/verify-email")
                || path.contains("/auth/suspect-alert")
                || path.equals("/rawabet/users/add")
                || path.startsWith("/rawabet/ws/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token        = authHeader.substring(7);
            String email        = jwtService.extractEmail(token);
            int    tokenVersion = jwtService.extractTokenVersion(token);   // ← méthode ajoutée

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userRepository.findByEmail(email).orElse(null);

                if (user != null && user.getTokenVersion() == tokenVersion) {

                    // ── Vérification ban temporaire ──────────────────────────
                    // Un ban temporaire expiré → réactiver silencieusement
                    boolean isBanned = false;
                    if (!user.isActive()) {
                        if (user.getBanUntil() != null
                                && LocalDateTime.now().isAfter(user.getBanUntil())) {
                            // Ban expiré → réactiver (le prochain login le fera proprement)
                            // Ici on laisse passer pour ne pas bloquer les sessions légitimes
                            isBanned = false;
                        } else {
                            isBanned = true;
                        }
                    }

                    if (!isBanned) {
                        // Construire les authorities depuis les permissions des rôles
                        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                                .flatMap(role -> Stream.concat(
                                        Stream.of(new SimpleGrantedAuthority(role.getName())),
                                        role.getPermissions().stream()
                                                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                                ))
                                .distinct()
                                .toList();

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(user, null, authorities);

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }

        } catch (Exception e) {
            // Token invalide, expiré ou malformé → pas d'authentification
            System.err.println("JWT error: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
