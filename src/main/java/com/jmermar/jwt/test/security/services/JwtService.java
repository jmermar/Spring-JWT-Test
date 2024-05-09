package com.jmermar.jwt.test.security.services;

import com.jmermar.jwt.test.model.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    private final String secret = "8871e0336765efc2ef851883e3ac9ad618cb3f642dc68c7d6e9aa3ed40588b1b";

    public String generateJwtToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();

        return Jwts.builder().subject(userPrincipal.getEmail())
                .issuedAt(new Date()).signWith(getSecretKey()).compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Base64.decodeBase64(secret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateJsonToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
