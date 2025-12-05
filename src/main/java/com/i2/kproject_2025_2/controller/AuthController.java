package com.i2.kproject_2025_2.controller;

import com.i2.kproject_2025_2.dto.AuthResponse;
import com.i2.kproject_2025_2.dto.LoginRequest;
import com.i2.kproject_2025_2.dto.PasswordResetRequest;
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

    @Tag(name = "Auth - Sign Up", description = "회원가입 및 이메일 인증")
    @Operation(summary = "1. 이메일 인증 코드 발송", description = "학교 메일(@kangnam.ac.kr)로 인증 코드를 발송합니다...")
    @PostMapping("/send-verification-code")
    public ResponseEntity<Void> sendVerificationCode(@RequestParam String email) {
        authService.sendVerificationCode(email);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Auth - Sign Up")
    @Operation(summary = "2. 이메일 인증 코드 확인", description = "발송된 인증 코드를 확인합니다.")
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        authService.verifyCode(email, code);
        return ResponseEntity.ok("이메일 인증 완료!");
    }

    @Tag(name = "Auth - Sign Up")
    @Operation(summary = "3. 회원가입", description = "인증된 이메일과 비밀번호로 회원가입을 완료합니다.")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest req) {
        authService.signup(req);
        return ResponseEntity.ok().build();
    }

    // ---------------------- 🔐 로그인 ----------------------
    @Tag(name = "Auth - Login", description = "로그인 및 JWT 발급")
    @Operation(summary = "로그인", description = "회원가입 및 이메일 인증 완료 후, 이메일과 비밀번호로 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // ---------------------- 🔒 비밀번호 찾기 ----------------------
    @Tag(name = "Auth - Password Reset", description = "비밀번호 찾기")
    @Operation(summary = "비밀번호 재설정 코드 발송", description = "가입된 이메일로 6자리 인증 코드를 발송합니다.")
    @PostMapping("/password-reset/send-code")
    public ResponseEntity<Void> sendPasswordResetCode(@RequestParam String email) {
        authService.sendPasswordResetCode(email);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Auth - Password Reset")
    @Operation(summary = "비밀번호 재설정 완료", description = "이메일+코드를 검증하고 새 비밀번호로 변경합니다.")
    @PostMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok().build();
    }
}
