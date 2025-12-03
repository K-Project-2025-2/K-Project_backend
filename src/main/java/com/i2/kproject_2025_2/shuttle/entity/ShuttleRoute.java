package com.i2.kproject_2025_2.shuttle.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "shuttle_routes")
@Getter
@Setter
public class ShuttleRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shuttle_route_stations", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "station_name")
    @OrderColumn(name = "station_order")
    private List<String> stations;
}
