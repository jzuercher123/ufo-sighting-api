package com.ufomap.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sightings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sighting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    @Column(length = 100)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 100)
    private String country;

    private String shape;

    private String duration;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String posted;

    private Double latitude;

    private Double longitude;

    private String submittedBy;

    private LocalDateTime submissionDate;

    private boolean userSubmitted;

    private String submissionStatus; // "pending", "approved", "rejected"
}