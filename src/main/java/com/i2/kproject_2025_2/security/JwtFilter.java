package com.i2.kproject_2025_2.security;

import com.i2.kproject_2025_2.entity.User;
import com.i2.kproject_2025_2.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// @ConditionalOnBean(UserRepository.class) // ✅ 빈 생성 순서 문제를 유발하므로 제거
@Component
@RequiredArgsConstructor
@Slf4j
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

        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        if (isWhitelisted(uri)) {
            chain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtUtil.parseClaims(token);
            String email = claims.getSubject();
            String roleClaim = claims.get("role", String.class);

            if (email == null || email.isBlank()) {
                chain.doFilter(request, response);
                return;
            }

            String mappedRole = (roleClaim != null && roleClaim.startsWith("ROLE_"))
                    ? roleClaim
                    : (roleClaim != null && !roleClaim.isBlank() ? "ROLE_" + roleClaim : "ROLE_USER");

            // --- 상세 디버깅 로그 시작 ---
            Optional<User> optionalUser = userRepository.findByEmail(email);

            if (optionalUser.isPresent()) {
                log.info("✅ User '{}' found in DB. Setting authentication in SecurityContext.", email);
            } else {
                log.warn("⚠️ User '{}' NOT found in DB. Still proceeding to set authentication based on token info.", email);
            }

            var auth = new UsernamePasswordAuthenticationToken(
                    email, null, List.of(new SimpleGrantedAuthority(mappedRole)));
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("✅ Authentication object successfully set in SecurityContext for '{}'.", email);
            // --- 상세 디버깅 로그 끝 ---

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("❌ Invalid JWT token processing for token: {}. Error: {}", token, e.getMessage());
        }

        chain.doFilter(request, response);
    }

    private boolean isWhitelisted(String uri) {
        // ... (이하 동일)
        if (uri.startsWith("/v3/api-docs/")
                || uri.startsWith("/swagger-ui/")
                || "/swagger-ui.html".equals(uri)) {
            return true;
        }
        if ("/actuator/health".equals(uri) || "/api/health".equals(uri)) {
            return true;
        }
        if (uri.startsWith("/api/devmail/")) {
            return true;
        }
        if (uri.startsWith("/api/auth/")) {
            return true;
        }
        return false;
    }
}
