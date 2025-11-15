package com.i2.kproject_2025_2.config;

import com.i2.kproject_2025_2.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class SecurityConfig {

    // ✅ 선택 주입: JwtFilter가 빈으로 있을 때만 주입
    @Autowired(required = false)
    private JwtFilter jwtFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // JWT 사용 시 CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // CORS 설정 사용
                .cors(Customizer.withDefaults())
                // 세션 미사용(완전 무상태)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 예외 처리(401/403 명확화)
                .exceptionHandling(ex -> ex
                        // 인증 안 됨
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(401);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"unauthorized\"}");
                        })
                        // 권한 없음
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(403);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"forbidden\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // ✅ Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ 완전 공개(스웨거/헬스/에러)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/health",
                                "/api/health",
                                "/error"
                        ).permitAll()

                        // ✅ 인증 없이 접근 가능한 인증 관련 엔드포인트들
                        .requestMatchers(
                                "/api/auth/send-verification-code",
                                "/api/auth/verify-code",
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/devmail/**"
                        ).permitAll()

                        // ✅ 그 외는 인증 필요
                        .anyRequest().authenticated()
                );

        // ✅ JwtFilter가 존재할 때만 등록 (UsernamePasswordAuthenticationFilter 앞에)
        if (jwtFilter != null) {
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // ✅ 필요한 오리진 추가(로컬 + EC2 퍼블릭 도메인/아이피 예시)
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://3.36.32.57",          // EC2 IP (HTTP)
                "https://3.36.32.57",         // EC2 IP (HTTPS, ALB/Certificate 적용 시)
                "https://api.example.com"     // 도메인 연결 시 예시
        ));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
        config.setExposedHeaders(List.of("Location"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}