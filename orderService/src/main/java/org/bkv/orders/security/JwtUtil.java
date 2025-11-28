package org.bkv.orders.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.access-expiration}")
    private long jwtAccessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String userName) {
        return Jwts.builder()
                .subject(userName)
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtAccessExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String userName) {
        return Jwts.builder()
                .subject(userName)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtRefreshExpiration))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
            String type = claims.get("type", String.class);
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUserName(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}