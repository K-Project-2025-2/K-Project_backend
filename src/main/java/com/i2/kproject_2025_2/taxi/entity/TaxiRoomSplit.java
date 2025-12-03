package com.i2.kproject_2025_2.taxi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "taxi_room_split",
        uniqueConstraints = @UniqueConstraint(columnNames = "room_id"))
public class TaxiRoomSplit {

    public enum Status { PENDING, COMPLETED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private TaxiRoom room;

    @Column(nullable = false)
    private int totalAmount;

    @Column(nullable = false)
    private int amountPerPerson;

    @Column(nullable = false)
    private int memberCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public TaxiRoom getRoom() { return room; }
    public void setRoom(TaxiRoom room) { this.room = room; }
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
    public int getAmountPerPerson() { return amountPerPerson; }
    public void setAmountPerPerson(int amountPerPerson) { this.amountPerPerson = amountPerPerson; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
