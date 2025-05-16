package com.ufomap.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SightingDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTime;

    @Size(max = 100)
    private String city;

    @Size(max = 50)
    private String state;

    @Size(max = 100)
    private String country;

    @Size(max = 50)
    private String shape;

    @Size(max = 100)
    private String duration;

    @Size(max = 5000)
    private String summary;

    private String posted;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String submittedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submissionDate;

    private boolean isUserSubmitted;

    private String submissionStatus;
}