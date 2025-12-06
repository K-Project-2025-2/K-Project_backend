package com.i2.kproject_2025_2.repository;

import com.i2.kproject_2025_2.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmail(String email);
    void deleteByEmail(String email);
}
