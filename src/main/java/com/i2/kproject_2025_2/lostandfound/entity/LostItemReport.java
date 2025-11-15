package com.i2.kproject_2025_2.lostandfound.entity;

import com.i2.kproject_2025_2.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class LostItemReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id")
    private User reporter;

    private String location;
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
