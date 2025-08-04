package com.lumicare.weerobot.domain.emotion.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String headertoken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (headertoken == null || !headertoken.startsWith("Bearer ")) {
            // 토큰이 없거나 'Bearer '로 시작하지 않으면 넘어감
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 추출
        String token = headertoken.split(" ")[1];

        // 토큰이 유효한지 검사
        if (jwtTokenProvider.validateToken(token)) {
            // 토큰에서 인증 정보를 가져와서 SecurityContext에 설정
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 필터 체인 진행 (다음 필터 실행)
        filterChain.doFilter(request, response);
    }
}