package com.i2.kproject_2025_2.controller;

import com.i2.kproject_2025_2.dto.AuthResponse;
import com.i2.kproject_2025_2.dto.LoginRequest;
import com.i2.kproject_2025_2.dto.SignupRequest;
import com.i2.kproject_2025_2.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ---------------------- 🧩 회원가입 ----------------------
    @Tag(name = "Auth - Sign Up", description = "회원가입 및 이메일 인증")
    @Operation(summary = "회원가입", description = "학교 메일(@kangnam.ac.kr)만 가입 가능하며, 이메일 인증 링크가 발송됩니다.")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest req) {
        authService.signup(req);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Auth - Sign Up")
    @Operation(summary = "이메일 인증", description = "회원가입 후 이메일로 전송된 인증 링크 클릭 시 호출됩니다.")
    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        authService.verify(token);
        return ResponseEntity.ok("이메일 인증 완료! 이제 로그인하세요.");
    }

    // ---------------------- 🔐 로그인 ----------------------
    @Tag(name = "Auth - Login", description = "로그인 및 JWT 발급")
    @Operation(summary = "로그인", description = "회원가입 및 이메일 인증 완료 후, 아이디와 비밀번호로 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
