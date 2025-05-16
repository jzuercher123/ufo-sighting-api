package com.ufomap.api.service;

import com.ufomap.api.dto.SightingDTO;
import com.ufomap.api.exception.ResourceNotFoundException;
import com.ufomap.api.model.Sighting;
import com.ufomap.api.repository.SightingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SightingService {

    private final SightingRepository sightingRepository;

    public List<SightingDTO> getAllSightings(SpringDataWebProperties.Pageable pageable) {
        return sightingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SightingDTO getSightingById(Long id) {
        Sighting sighting = sightingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sighting not found with id: " + id));
        return convertToDTO(sighting);
    }

    public List<SightingDTO> getSightingsWithFilters(
            String shape,
            String city,
            String country,
            String state,
            String searchText, Pageable pageable) {

        return sightingRepository.findWithFilters(shape, city, country, state, searchText).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SightingDTO> getSightingsInBounds(Double north, Double south, Double east, Double west, Pageable pageable) {
        return sightingRepository.findInBounds(north, south, east, west).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SightingDTO createSighting(SightingDTO sightingDTO) {
        sightingDTO.setSubmissionDate(LocalDateTime.now());
        sightingDTO.setSubmissionStatus("pending");
        sightingDTO.setUserSubmitted(true);

        Sighting sighting = convertToEntity(sightingDTO);
        Sighting savedSighting = sightingRepository.save(sighting);

        return convertToDTO(savedSighting);
    }

    public SightingDTO updateSightingStatus(Long id, String status) {
        Sighting sighting = sightingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sighting not found with id: " + id));

        sighting.setSubmissionStatus(status);
        Sighting updatedSighting = sightingRepository.save(sighting);

        return convertToDTO(updatedSighting);
    }

    // Helper methods for entity-DTO conversion
    private SightingDTO convertToDTO(Sighting sighting) {
        return SightingDTO.builder()
                .id(sighting.getId())
                .dateTime(sighting.getDateTime())
                .city(sighting.getCity())
                .state(sighting.getState())
                .country(sighting.getCountry())
                .shape(sighting.getShape())
                .duration(sighting.getDuration())
                .summary(sighting.getSummary())
                .posted(sighting.getPosted())
                .latitude(sighting.getLatitude())
                .longitude(sighting.getLongitude())
                .submittedBy(sighting.getSubmittedBy())
                .submissionDate(sighting.getSubmissionDate())
                .isUserSubmitted(sighting.isUserSubmitted())
                .submissionStatus(sighting.getSubmissionStatus())
                .build();
    }

    private Sighting convertToEntity(SightingDTO dto) {
        return Sighting.builder()
                .id(dto.getId())
                .dateTime(dto.getDateTime())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .shape(dto.getShape())
                .duration(dto.getDuration())
                .summary(dto.getSummary())
                .posted(dto.getPosted())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .submittedBy(dto.getSubmittedBy())
                .submissionDate(dto.getSubmissionDate())
                .userSubmitted(dto.isUserSubmitted())
                .submissionStatus(dto.getSubmissionStatus())
                .build();
    }
}