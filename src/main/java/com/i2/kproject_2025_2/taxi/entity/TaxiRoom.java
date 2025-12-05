package com.i2.kproject_2025_2.taxi.entity;

import com.i2.kproject_2025_2.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "taxi_room")
public class TaxiRoom {

    public enum Status { OPEN, FULL }
    public enum OperationStatus { STARTED, ACCEPTED, DEPARTED, ENDED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 6, unique = true, nullable = false)
    private String roomCode;

    @Column(nullable = false)
    private String meetingPoint;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime meetingTime;

    @Column(nullable = false)
    private int capacity; // 2~4

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus; // 운행 진행 상태

    private LocalDateTime operationStartedAt;
    private LocalDateTime operationDepartedAt;
    private LocalDateTime operationEndedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Getters / Setters
    public Long getId() { return id; }
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public String getMeetingPoint() { return meetingPoint; }
    public void setMeetingPoint(String meetingPoint) { this.meetingPoint = meetingPoint; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDateTime getMeetingTime() { return meetingTime; }
    public void setMeetingTime(LocalDateTime meetingTime) { this.meetingTime = meetingTime; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public OperationStatus getOperationStatus() { return operationStatus; }
    public void setOperationStatus(OperationStatus operationStatus) { this.operationStatus = operationStatus; }
    public LocalDateTime getOperationStartedAt() { return operationStartedAt; }
    public void setOperationStartedAt(LocalDateTime operationStartedAt) { this.operationStartedAt = operationStartedAt; }
    public LocalDateTime getOperationDepartedAt() { return operationDepartedAt; }
    public void setOperationDepartedAt(LocalDateTime operationDepartedAt) { this.operationDepartedAt = operationDepartedAt; }
    public LocalDateTime getOperationEndedAt() { return operationEndedAt; }
    public void setOperationEndedAt(LocalDateTime operationEndedAt) { this.operationEndedAt = operationEndedAt; }
    public User getLeader() { return leader; }
    public void setLeader(User leader) { this.leader = leader; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

