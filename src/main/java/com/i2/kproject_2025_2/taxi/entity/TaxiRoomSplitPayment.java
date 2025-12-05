package com.i2.kproject_2025_2.taxi.entity;

import com.i2.kproject_2025_2.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "taxi_room_split_payment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"split_id", "user_id"}))
public class TaxiRoomSplitPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "split_id", nullable = false)
    private TaxiRoomSplit split;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime paidAt;

    public Long getId() { return id; }
    public TaxiRoomSplit getSplit() { return split; }
    public void setSplit(TaxiRoomSplit split) { this.split = split; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getPaidAt() { return paidAt; }
}
