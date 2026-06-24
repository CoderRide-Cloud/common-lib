package com.codingclub.common.util;

import com.codingclub.common.security.Permission;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret:thisisaverysecuresecretkeythatisverylongandsecure!}")
    private String secret;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationTime;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(
            Long userId,
            String role,
            Long customRoleId,
            Integer position,
            Boolean isLead,
            Boolean isActive,
            Set<Permission> permissions) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        if (customRoleId != null) {
            claims.put("customRoleId", customRoleId);
        }
        claims.put("position", position != null ? position : 0);
        claims.put("isLead", Boolean.TRUE.equals(isLead));
        claims.put("isActive", isActive == null || isActive);
        if (permissions != null && !permissions.isEmpty()) {
            claims.put("permissions", permissions.stream().map(Enum::name).collect(Collectors.joining(",")));
        }
        return createToken(claims, String.valueOf(userId));
    }

    public String generateToken(Long userId, String role) {
        return generateToken(userId, role, null, 0, false, true, Collections.emptySet());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
