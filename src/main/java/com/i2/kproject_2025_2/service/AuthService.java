package com.i2.kproject_2025_2.service;

import com.i2.kproject_2025_2.dto.LoginRequest;
import com.i2.kproject_2025_2.dto.RegisterRequest;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final EmailVerificationTokenRepository tokenRepo;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;

    @Value("${app.allowed-email-domain}")
    private String allowedDomain;

    private void assertSchoolEmail(String email) {
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase(Locale.ROOT);
        if (!domain.equals(allowedDomain)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "학교 이메일(@" + allowedDomain + ")만 가입 가능합니다.");
        }
    }

    @Transactional
    public void register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase(Locale.ROOT);
        assertSchoolEmail(email);

        if (userRepo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(req.password()));
        user.setEnabled(false);
        userRepo.save(user);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(2));
        tokenRepo.save(token);

        sendVerifyMail(email, token.getToken());
    }

    private void sendVerifyMail(String to, String token) {
        String link = "http://localhost:8080/api/auth/verify?token=" + token; // 필요 시 설정값으로 추출
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("[K-Project] 이메일 인증 링크");
        msg.setText("아래 링크를 2시간 이내에 클릭해 이메일을 인증하세요.\n" + link);
        mailSender.send(msg);
    }

    @Transactional
    public void verify(String token) {
        var t = tokenRepo.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "토큰이 유효하지 않습니다."));
        if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "토큰이 만료되었습니다.");
        }
        var user = t.getUser();
        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepo.save(user);
        }
        tokenRepo.delete(t); // 1회용
    }

    public String login(LoginRequest req) {
        String email = req.email().trim().toLowerCase(Locale.ROOT);
        assertSchoolEmail(email);

        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다.");
        }
        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return jwtUtil.generate(user.getEmail(), user.getRole().name());
    }
}
