package com.i2.kproject_2025_2.auth;

import java.security.SecureRandom;

public final class VerificationCodeUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private VerificationCodeUtil() {}

    /** 000000 ~ 999999 범위의 6자리 숫자 문자열 */
    public static String numeric6() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}
