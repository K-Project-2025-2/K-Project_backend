package com.i2.kproject_2025_2.shuttle.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "shuttle_timetables")
@Getter
@Setter
public class ShuttleTimetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private ShuttleRoute route;

    @Column(nullable = false)
    private LocalTime departureTime;

    @Column
    private String busNumber;

    @Column
    private String description;
}
