package com.lumicare.weerobot.domain.emotion.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String secretKey;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // 평문 그대로 사용
    }

    // 토큰 생성
    public String generateToken (String id){
        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 사용자 정보 추출
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        // userId만 추출
        String userId = claims.getSubject();  // Subject는 userId로 사용

        UserDetails principal = new CustomUser(userId, userId, "", new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(principal, "", new ArrayList<>());
    }

    // 토큰에서 Claims 추출
    private Claims getClaims(String token) {
        return Jwts.parser()
//                .setSigningKey(secretKey)
                .setSigningKey(getKey())  // ✅ base64 URL-safe 대응
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}