package com.insure.insurebackend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET =
            "mySuperSecretKeyForJwtAuthenticationMySuperSecretKey";

    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 24 hours

    // 🔐 Generate SecretKey (IMPORTANT: must be SecretKey, not Key)
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ================= GENERATE TOKEN =================
    public String generateToken(String username, String role) {

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    // ================= VALIDATE TOKEN =================
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())   // ✅ SecretKey only
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (SignatureException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // ================= EXTRACT USERNAME =================
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ================= EXTRACT ROLE =================
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ================= GET CLAIMS =================
    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())   // ✅ SecretKey only
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
