package com.i2.kproject_2025_2.config;

import com.i2.kproject_2025_2.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 핵심: CORS 활성화 + OPTIONS 허용 + Stateless + JWT 필터 삽입
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // JWT 기반이면 CSRF는 보통 비활성화
                .csrf(csrf -> csrf.disable())

                // CORS 설정 활성화 (아래 corsConfigurationSource() 빈 사용)
                .cors(Customizer.withDefaults())

                // 권한 규칙
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트(OPTIONS) 무조건 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 공개 엔드포인트 (회원가입/로그인/검증/헬스체크/개발용 메일)
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/actuator/health",
                                "/api/health",
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/verify",
                                "/api/devmail/**"
                        ).permitAll()

                        // 그 외는 인증 필요
                        .anyRequest().authenticated()
                )

                // 세션 사용 안 함
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // UsernamePasswordAuthenticationFilter 전에 JWT 필터 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 전역 정책
     * - 프론트엔드 주소를 실제 값으로 반드시 교체할 것!
     *   예) http://localhost:3000, http://127.0.0.1:5173, https://your-frontend.app 등
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ★ 반드시 실제 프론트 주소로 바꿔주세요!
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000"
                // "http://localhost:5173",
                // "https://your-frontend.app"
        ));

        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
        config.setExposedHeaders(List.of("Location")); // 필요 시 노출 헤더 추가
        config.setAllowCredentials(true);              // 쿠키/Authorization 허용
        config.setMaxAge(3600L);                       // preflight 캐시 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 CORS 정책 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
