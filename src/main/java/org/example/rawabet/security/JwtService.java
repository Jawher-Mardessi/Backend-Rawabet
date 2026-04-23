package org.example.rawabet.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.rawabet.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private long expirationMs;

    private static final long IMPERSONATION_EXPIRATION_MS = 30L * 60 * 1000;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ── Token normal ───────────────────────────────────────────────────────
    public String generateToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName()).toList();
        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getName()).distinct().toList();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId",        user.getId())
                .claim("name",          user.getNom())
                .claim("email",         user.getEmail())
                .claim("tokenVersion",  user.getTokenVersion())
                .claim("roles",         roles)
                .claim("permissions",   permissions)
                .claim("impersonation", false)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Token d'impersonation ──────────────────────────────────────────────
    /**
     * Génère un token CLIENT UNIQUEMENT pour le mode impersonation.
     *
     * CRITIQUE : même si l'admin a les rôles [SUPER_ADMIN, CLIENT],
     * le token d'impersonation ne doit contenir QUE le rôle CLIENT
     * et ses permissions (FIDELITY_READ, FIDELITY_UPDATE).
     *
     * Sans ce filtre : isAdmin() = true → navbar affiche "Super Admin",
     * "Réserver" est masqué → l'admin ne se comporte PAS comme un vrai client.
     */
    public String generateImpersonationToken(User target, Long adminId) {

        // Filtrer UNIQUEMENT le rôle CLIENT — exclure tous les rôles admin
        List<String> clientOnlyRoles = target.getRoles().stream()
                .map(r -> r.getName())
                .filter(name -> name.equals("CLIENT"))
                .toList();

        // Permissions du rôle CLIENT uniquement
        List<String> clientOnlyPermissions = target.getRoles().stream()
                .filter(r -> r.getName().equals("CLIENT"))
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getName())
                .distinct()
                .toList();

        return Jwts.builder()
                .setSubject(target.getEmail())
                .claim("userId",         target.getId())
                .claim("name",           target.getNom())
                .claim("email",          target.getEmail())
                .claim("tokenVersion",   target.getTokenVersion())
                .claim("roles",          clientOnlyRoles)
                .claim("permissions",    clientOnlyPermissions)
                .claim("impersonation",  true)
                .claim("impersonatedBy", adminId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + IMPERSONATION_EXPIRATION_MS))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Extraction ─────────────────────────────────────────────────────────
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * MÉTHODE MANQUANTE — utilisée par JwtFilter ligne 53.
     * Extrait le tokenVersion pour valider que le token n'est pas révoqué
     * (ban, changement mot de passe, logout forcé).
     */
    public int extractTokenVersion(String token) {
        Object version = extractClaims(token).get("tokenVersion");
        if (version == null)              return 0;
        if (version instanceof Integer)   return (Integer) version;
        if (version instanceof Long)      return ((Long) version).intValue();
        if (version instanceof Number)    return ((Number) version).intValue();
        return 0;
    }

    /**
     * Extrait le flag impersonation.
     * true = token généré par POST /auth/impersonate
     */
    public boolean extractIsImpersonation(String token) {
        Object flag = extractClaims(token).get("impersonation");
        if (flag instanceof Boolean) return (Boolean) flag;
        return false;
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, String email) {
        try {
            Claims claims = extractClaims(token);
            return claims.getSubject().equals(email)
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}