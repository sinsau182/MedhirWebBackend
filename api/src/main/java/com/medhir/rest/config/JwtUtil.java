package com.medhir.rest.config;

import com.medhir.rest.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String email, Set<Role> roles) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles.stream().map(Enum::name).collect(Collectors.toList())) // Convert enum to String
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Set<Role> extractRoles(String token) {
        List<String> roleStrings = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().get("roles", List.class);
        return roleStrings.stream().map(Role::valueOf).collect(Collectors.toSet()); // Convert String back to Enum
    }

    public boolean isValidToken(String token, String email) {
        return email.equals(extractEmail(token)) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
