package org.example.rawabet.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.rawabet.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private long expirationMs;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getNom())
                .claim("email", user.getEmail())
                .claim("tokenVersion", user.getTokenVersion())
                .claim("roles", user.getRoles().stream()
                        .map(role -> role.getName())
                        .toList())
                .claim("permissions", user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(permission -> permission.getName())
                        .distinct()
                        .toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public int extractTokenVersion(String token) {
        Object raw = extractClaims(token).get("tokenVersion");
        if (raw instanceof Integer i) {
            return i;
        }
        if (raw instanceof Number n) {
            return n.intValue();
        }
        return 0;
    }
}