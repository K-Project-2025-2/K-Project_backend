package com.i2.kproject_2025_2.config;

import com.i2.kproject_2025_2.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        // -- Swagger / Actuator --
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                        "/actuator/health", "/api/health",

                        // -- Auth --
                        "/api/auth/signup", "/api/auth/login", "/api/auth/verify",

                        // -- Shuttle (Public) --
                        "/shuttle/routes", "/shuttle/timetable", "/shuttle/locations", "/shuttle/congestion"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/shuttle/favorites").authenticated()
                .requestMatchers(HttpMethod.POST, "/shuttle/favorites").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/shuttle/favorites/**").authenticated()
                .anyRequest().authenticated()
        );

        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
