package com.i2.kproject_2025_2.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key key;
    private final String issuer;
    private final long expMinutes;

    /**
     * application.yml 에서 설정 주입:
     * jwt.secret: 최소 256-bit(32바이트) 이상 Base64 인코딩 추천
     * jwt.issuer: 발급자
     * jwt.exp-minutes: 만료 시간(분)
     */
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer:K-Project}") String issuer,
            @Value("${jwt.exp-minutes:120}") long expMinutes
    ) {
        // secret 이 Base64가 아닐 경우: Keys.hmacShaKeyFor(secret.getBytes())
        // secret 이 Base64일 경우(권장): Decoders.BASE64.decode(secret)
        byte[] keyBytes = (isBase64(secret) ? Decoders.BASE64.decode(secret) : secret.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.expMinutes = expMinutes;
    }

    private boolean isBase64(String s) {
        try {
            Decoders.BASE64.decode(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰 생성
     * @param subject  보통 email/username
     * @param role     예: "USER" 또는 "ADMIN" (접두사 없이 저장 권장)
     * @return         JWT 문자열
     */
    public String generate(String subject, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expMinutes * 60);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(Map.of(
                        "role", role   // JwtFilter에서 꺼내는 클레임 이름과 일치해야 함
                ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 서명/만료 검증 + 클레임 파싱
     */
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(issuer) // issuer 일치 강제(원치 않으면 제거 가능)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
