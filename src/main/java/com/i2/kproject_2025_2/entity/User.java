package com.i2.kproject_2025_2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;   // 로그인용 아이디

    @Column(unique = true, nullable = false)
    private String email;      // 학교 메일 (인증용)

    @Column(nullable = false)
    private String password;   // BCrypt 해시

    @Column(nullable = false)
    private boolean enabled = false;  // 이메일 인증 완료 여부

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    // 프로필 정보
    private String name;       // 이름
    private String studentId;  // 학번
    private String phone;      // 전화번호

    // 알림 설정
    private boolean pushNotifications = true;
    private boolean shuttleAlert = true;
    private boolean taxiAlert = false;

    // 보증금 정보
    private int depositAmount = 0;
    private boolean depositLocked = true;

    public enum Role { USER, ADMIN }
}
