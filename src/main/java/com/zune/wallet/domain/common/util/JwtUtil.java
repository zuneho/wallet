package com.zune.wallet.domain.common.util;

import com.zune.wallet.api.auth.service.model.MemberDetailDto;
import com.zune.wallet.api.common.exception.AuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    private static final long TOKEN_EXPIRED_HOUR = 60 * 60; // 1 hour in seconds
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(MemberDetailDto member) {
        Instant now = Instant.now(); //for UTC
        Instant tokenValidity = now.plus(TOKEN_EXPIRED_HOUR, ChronoUnit.SECONDS);

        return Jwts.builder()
                .claim("id", member.getId())
                .claim("name", member.getName())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(tokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserId(String token) {
        return parseClaims(token).get("id", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            log.info("Unsupported or empty JWT Token", e);
        }
        throw new AuthException("Invalid token");
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
