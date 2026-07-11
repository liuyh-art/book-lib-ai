package com.example.book.util;

import com.example.book.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Autowired private JwtConfig jwtConfig;
    public String generateToken(Long userId, String username, Integer role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getExpiration());
        return Jwts.builder().subject(String.valueOf(userId)).claim("username", username).claim("role", role)
                .issuedAt(now).expiration(expiration).signWith(getSigningKey()).compact();
    }
    public Claims parseToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
    private SecretKey getSigningKey() { return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret())); }
    public boolean validateToken(String token) { try { parseToken(token); return true; } catch (JwtException | IllegalArgumentException e) { return false; } }
    public Long getUserId(String token) { return Long.parseLong(parseToken(token).getSubject()); }
    public String getUsername(String token) { return parseToken(token).get("username", String.class); }
    public Integer getRole(String token) { return parseToken(token).get("role", Integer.class); }
}
