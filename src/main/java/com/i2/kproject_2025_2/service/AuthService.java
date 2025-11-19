package com.i2.kproject_2025_2.service;

import com.i2.kproject_2025_2.dto.LoginRequest;
import com.i2.kproject_2025_2.dto.SignupRequest;
import com.i2.kproject_2025_2.entity.EmailVerificationToken;
import com.i2.kproject_2025_2.entity.User;
import com.i2.kproject_2025_2.repository.EmailVerificationTokenRepository;
import com.i2.kproject_2025_2.repository.UserRepository;
import com.i2.kproject_2025_2.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final EmailVerificationTokenRepository verificationRepo;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;

    @Value("${app.allowed-email-domain}")
    private String allowedDomain;

    private void assertSchoolEmail(String email) {
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase(Locale.ROOT);
        if (!domain.equals(allowedDomain)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "학교 이메일(@" + allowedDomain + ")만 가입 가능합니다.");
        }
    }

    @Transactional
    public void sendVerificationCode(String email) {
        email = email.trim().toLowerCase();
        assertSchoolEmail(email);

        if (userRepo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 이메일입니다.");
        }

        String code = generateVerificationCode();
        EmailVerificationToken verification = verificationRepo.findByEmail(email)
                .orElse(new EmailVerificationToken());

        verification.setEmail(email);
        verification.setCode(code);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // 10분 후 만료
        verification.setVerified(false);
        verificationRepo.save(verification);

        sendVerificationMail(email, code);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 인증 코드 생성
        return String.valueOf(code);
    }

    private void sendVerificationMail(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("[K-Project] 이메일 인증 코드");
        msg.setText("인증 코드는 " + code + " 입니다. 10분 이내에 입력해주세요.");
        mailSender.send(msg);
    }

    @Transactional
    public void verifyCode(String email, String code) {
        email = email.trim().toLowerCase();
        EmailVerificationToken verification = verificationRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "인증 코드가 발송되지 않은 이메일입니다."));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "인증 코드가 만료되었습니다.");
        }

        if (!verification.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "인증 코드가 올바르지 않습니다.");
        }

        verification.setVerified(true);
        verificationRepo.save(verification);
    }

    @Transactional
    public void signup(SignupRequest req) {
        String email = req.email().trim().toLowerCase();

        EmailVerificationToken verification = verificationRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일 인증이 필요합니다."));

        if (!verification.isVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다.");
        }
        
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "인증 세션이 만료되었습니다. 다시 인증해주세요.");
        }

        if (userRepo.existsByUsername(req.username()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        if (userRepo.existsByEmail(email))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 이메일입니다.");

        User user = new User();
        user.setUsername(req.username().trim().toLowerCase());
        user.setEmail(email);
        user.setPassword(encoder.encode(req.password()));
        user.setEnabled(true); // 바로 활성화
        userRepo.save(user);

        verificationRepo.delete(verification); // 회원가입 완료 후 인증 정보 삭제
    }

    public String login(LoginRequest req) {
        var user = userRepo.findByEmail(req.email().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!user.isEnabled())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비활성화된 계정입니다.");
        if (!encoder.matches(req.password(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");

        return jwtUtil.generate(user.getEmail(), user.getRole().name());
    }
}
