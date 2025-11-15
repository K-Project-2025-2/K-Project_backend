package com.i2.kproject_2025_2.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final StringRedisTemplate redis;

    private static final long TTL_MIN = 10;        // 코드 유효기간 10분
    private static final long COOLDOWN_SEC = 60;   // 재발송 쿨다운 60초
    private static final int MAX_ATTEMPTS = 5;     // 검증 시도 5회 제한

    private String keyCode(String email)     { return "verify:code:" + email; }
    private String keyCooldown(String email) { return "verify:cooldown:" + email; }
    private String keyAttempts(String email) { return "verify:attempts:" + email; }

    /** 재발송 쿨다운 중인지 확인 */
    public long checkCooldownSeconds(String email) {
        Long ttl = redis.getExpire(keyCooldown(email), TimeUnit.SECONDS);
        return ttl == null ? -1L : ttl; // null 대신 -1 반환
    }
    /** 코드 생성 & 저장 + 쿨다운 설정 */
    public void issueCode(String email, String code) {
        ValueOperations<String, String> ops = redis.opsForValue();
        // 코드 저장 + TTL
        ops.set(keyCode(email), code, TTL_MIN, TimeUnit.MINUTES);
        // 쿨다운 키 1값으로 생성 (값 의미 없음), TTL 60초
        ops.set(keyCooldown(email), "1", COOLDOWN_SEC, TimeUnit.SECONDS);
        // 시도 횟수 초기화 (존재하면 reset)
        redis.delete(keyAttempts(email));
    }

    /** 코드 검증: 성공 시 코드 삭제, 실패 시 시도+1 */
    public VerifyResult verify(String email, String inputCode) {
        ValueOperations<String, String> ops = redis.opsForValue();

        String stored = ops.get(keyCode(email));
        if (stored == null) {
            return VerifyResult.expiredOrNotIssued();
        }

        // 시도 횟수 조회
        String attemptsStr = ops.get(keyAttempts(email));
        int attempts = attemptsStr == null ? 0 : Integer.parseInt(attemptsStr);
        if (attempts >= MAX_ATTEMPTS) {
            // 잠금: 코드/시도 모두 제거하여 재발송 유도
            redis.delete(keyCode(email));
            redis.delete(keyAttempts(email));
            return VerifyResult.locked();
        }

        boolean ok = stored.equals(inputCode);
        if (!ok) {
            ops.increment(keyAttempts(email));
            // 시도 키 TTL은 코드 TTL과 동일하게 맞춤(처음 생성 시에만 적용됨)
            if (attemptsStr == null) {
                redis.expire(keyAttempts(email), TTL_MIN, TimeUnit.MINUTES);
            }
            int remaining = Math.max(0, MAX_ATTEMPTS - attempts - 1);
            return VerifyResult.mismatch(remaining);
        }

        // 성공: 단발성 사용 → 삭제
        redis.delete(keyCode(email));
        redis.delete(keyAttempts(email));
        return VerifyResult.success();
    }

    public record VerifyResult(boolean ok, String reason, Integer remainingAttempts) {
        public static VerifyResult success()                    { return new VerifyResult(true, null, null); }
        public static VerifyResult expiredOrNotIssued()         { return new VerifyResult(false, "코드가 만료되었거나 발급되지 않았습니다.", null); }
        public static VerifyResult locked()                     { return new VerifyResult(false, "시도 횟수 초과(잠금). 다시 발송하세요.", null); }
        public static VerifyResult mismatch(int remaining)      { return new VerifyResult(false, "코드가 일치하지 않습니다.", remaining); }
    }
}
