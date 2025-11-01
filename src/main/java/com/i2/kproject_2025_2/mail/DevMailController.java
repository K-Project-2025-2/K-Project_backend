package com.i2.kproject_2025_2.mail;

import com.i2.kproject_2025_2.auth.EmailVerificationService;
import com.i2.kproject_2025_2.auth.VerificationCodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/devmail")
@RequiredArgsConstructor
public class DevMailController {

    private final MailService mailService;
    private final EmailVerificationService verificationService;

    // 이메일 인증코드 발송 (도메인 제한 + 쿨다운 60초)
    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestParam String email) {
        String e = email.trim().toLowerCase();

        if (!e.endsWith("@kangnam.ac.kr")) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "reason", "강남대 메일만 허용됩니다."));
        }

        long cooldownLeft = verificationService.checkCooldownSeconds(e);
        if (cooldownLeft > 0) {
            return ResponseEntity.status(429).body(Map.of(
                    "ok", false,
                    "reason", "재발송은 " + cooldownLeft + "초 후 가능합니다."
            ));
        }

        String code = VerificationCodeUtil.numeric6();
        verificationService.issueCode(e, code);

        mailService.send(e, "[K-Project] 이메일 인증코드",
                """
                아래 인증코드를 10분 이내에 입력하세요.

                인증코드: %s
                (유효기간: 10분)
                """.formatted(code));

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "email", e,
                "ttlMinutes", 10,
                "cooldownSeconds", 60
        ));
    }

    // 이메일 인증코드 검증 (TTL 10분 + 시도 5회)
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String email, @RequestParam String code) {
        String e = email.trim().toLowerCase();

        var result = verificationService.verify(e, code.trim());
        if (!result.ok()) {
            if ("코드가 만료되었거나 발급되지 않았습니다.".equals(result.reason())) {
                return ResponseEntity.badRequest().body(Map.of("ok", false, "reason", result.reason()));
            }
            if ("시도 횟수 초과(잠금). 다시 발송하세요.".equals(result.reason())) {
                return ResponseEntity.status(423).body(Map.of("ok", false, "reason", result.reason()));
            }
            // 불일치
            return ResponseEntity.status(401).body(Map.of(
                    "ok", false,
                    "reason", result.reason(),
                    "remainingAttempts", result.remainingAttempts()
            ));
        }

        // 여기서 회원가입/로그인 플로우로 연결하거나 email_verified=true 처리
        return ResponseEntity.ok(Map.of("ok", true, "email", e, "verified", true));
    }
}
