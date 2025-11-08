package com.i2.kproject_2025_2.security;

import com.i2.kproject_2025_2.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@ConditionalOnBean(UserRepository.class)
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String method = request.getMethod();
        final String uri = request.getRequestURI();

        // 0) Preflight(OPTIONS)는 무조건 통과 (CORS)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        // 1) 공개 경로는 JWT 검사 스킵 (SecurityConfig의 permitAll과 일치시킬 것)
        if (isWhitelisted(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // 2) 이미 컨텍스트에 인증 정보가 있으면 재설정 불필요
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        // 3) Authorization 헤더 확인 (없으면 그냥 다음 필터로 위임)
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            // 4) JWT 파싱
            Claims claims = jwtUtil.parseClaims(token);
            String email = claims.getSubject();                 // sub
            String roleClaim = claims.get("role", String.class); // 예: "USER" 또는 "ROLE_USER"

            // 안전 가드: 이메일/롤 없으면 패스
            if (email == null || email.isBlank()) {
                chain.doFilter(request, response);
                return;
            }

            // role 매핑: "USER" -> "ROLE_USER", 이미 ROLE_ 붙어 있으면 그대로
            String mappedRole = (roleClaim != null && roleClaim.startsWith("ROLE_"))
                    ? roleClaim
                    : (roleClaim != null && !roleClaim.isBlank() ? "ROLE_" + roleClaim : "ROLE_USER");

            // (선택) 사용자 존재 확인 — 없어도 인증 세팅은 가능
            userRepository.findByEmail(email).ifPresentOrElse(user -> {
                var auth = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(new SimpleGrantedAuthority(mappedRole)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }, () -> {
                // 사용자 조회가 꼭 필요없다면, 주석 해제하여 리포지토리 미조회 시에도 인증 부여 가능
                var auth = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(new SimpleGrantedAuthority(mappedRole)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            });

        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않아도 여기서 바로 403 쓰지 말고
            // 인증 없이 다음 필터로 넘겨서 Security가 처리하게 둔다.
            // log.warn("Invalid JWT", e);
        }

        // 5) 다음 필터로 진행
        chain.doFilter(request, response);
    }

    /**
     * SecurityConfig의 permitAll 경로와 싱크를 맞춰주세요.
     */
    private boolean isWhitelisted(String uri) {
        // Swagger / Docs
        if (uri.startsWith("/v3/api-docs/")
                || uri.startsWith("/swagger-ui/")
                || "/swagger-ui.html".equals(uri)) {
            return true;
        }

        // 헬스체크
        if ("/actuator/health".equals(uri) || "/api/health".equals(uri)) {
            return true;
        }

        // 개발용 메일 엔드포인트
        if (uri.startsWith("/api/devmail/")) {
            return true;
        }

        // 인증 관련 공개 엔드포인트 (회원가입/로그인/이메일 인증)
        if (uri.startsWith("/api/auth/")) {
            // 예: /api/auth/signup, /api/auth/login, /api/auth/verify
            return true;
        }

        // 과거 경로 호환이 필요하면 추가 (예: /api/users/** 공개시)
        // if (uri.startsWith("/api/users/")) return true;

        return false;
    }
}
