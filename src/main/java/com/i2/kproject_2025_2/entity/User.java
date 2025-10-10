package com.i2.kproject_2025_2.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public enum Role { USER, ADMIN }

    // === Getter/Setter ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
