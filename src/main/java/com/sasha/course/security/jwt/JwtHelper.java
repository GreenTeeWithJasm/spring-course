package com.sasha.course.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtHelper {
    private final SecretKey key;

    @Value("${application.auth.issuer}")
    private String issuer;

    @Value("${application.auth.token_expire}")
    private long tokenExpiry;

    @Value("${application.auth.token_aud}")
    private String tokenAud;

    @Value("${application.auth.refresh_expire}")
    private long refreshExpiry;

    @Value("${application.auth.refresh_aud}")
    private String refreshAud;

    public JwtHelper(@Value("${application.auth.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public AccessTokens generateToken(String email) {
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuer(issuer)
                .setAudience(tokenAud)
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiry))
                .signWith(key)
                .compact();

        String refresh = Jwts.builder()
                .setSubject(email)
                .setIssuer(issuer)
                .setAudience(refreshAud)
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiry))
                .signWith(key)
                .compact();

        return new AccessTokens(token, refresh);
    }
}
