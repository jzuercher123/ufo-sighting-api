package com.ufomap.api.service;

import com.ufomap.api.dto.SightingDTO;
import com.ufomap.api.exception.ResourceNotFoundException;
import com.ufomap.api.model.Sighting;
// import com.ufomap.api.model.SubmissionStatus; // Not directly used as method param here, but good to have if status logic is complex
import com.ufomap.api.repository.SightingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
// Removed Collectors import as Page.map is used
// Removed List import as Page is used for paginated results

/**
 * Service class for handling operations related to sightings.
 * This class contains methods for retrieving, creating, and updating sightings,
 * as well as applying filters and geographical bounds to the queries.
 */
@Service
@RequiredArgsConstructor
public class SightingService {

    private final SightingRepository sightingRepository;

    public Page<SightingDTO> getAllSightings(Pageable pageable) {
        return sightingRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public SightingDTO getSightingById(Long id) {
        Sighting sighting = sightingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sighting not found with id: " + id));
        return convertToDTO(sighting);
    }

    public Sighting getSightingEntityById(Long id) {
        return sightingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sighting not found with id: " + id));
    }

    public Page<SightingDTO> getSightingsWithFilters(
            String shape,
            String city,
            String country,
            String state,
            String searchText, Pageable pageable) {
        return sightingRepository.findWithFilters(shape, city, country, state, searchText, pageable)
                .map(this::convertToDTO);
    }

    public Page<SightingDTO> getSightingsInBounds(Double north, Double south, Double east, Double west, Pageable pageable) {
        return sightingRepository.findInBounds(north, south, east, west, pageable)
                .map(this::convertToDTO);
    }

    public SightingDTO createSighting(SightingDTO sightingDTO) {
        sightingDTO.setSubmissionDate(LocalDateTime.now());
        sightingDTO.setSubmissionStatus("pending"); // Default status for new submissions
        sightingDTO.setUserSubmitted(true);       // Assuming createSighting is for user submissions

        Sighting sighting = convertToEntity(sightingDTO);
        Sighting savedSighting = sightingRepository.save(sighting);

        return convertToDTO(savedSighting);
    }

    public SightingDTO updateSighting(Long id, SightingDTO sightingDTO) {
        Sighting sighting = sightingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sighting not found with id: " + id));

        // Update editable fields from DTO
        // Be careful about which fields are truly updatable by a generic update method
        sighting.setDateTime(sightingDTO.getDateTime());
        sighting.setCity(sightingDTO.getCity());
        sighting.setState(sightingDTO.getState());
        sighting.setCountry(sightingDTO.getCountry());
        sighting.setShape(sightingDTO.getShape());
        sighting.setDuration(sightingDTO.getDuration());
        sighting.setSummary(sightingDTO.getSummary());
        sighting.setPosted(sightingDTO.getPosted()); // Or handle posting logic separately
        sighting.setLatitude(sightingDTO.getLatitude());
        sighting.setLongitude(sightingDTO.getLongitude());
        // Deliberately not updating: id, submittedBy, submissionDate, isUserSubmitted, submissionStatus via this generic method
        // submissionStatus should be updated via updateSightingStatus

        Sighting updatedSighting = sightingRepository.save(sighting);
        return convertToDTO(updatedSighting);
    }


    public SightingDTO updateSightingStatus(Long id, String status) { // Expecting String status from controller
        Sighting sighting = sightingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sighting not found with id: " + id));

        // Optional: Validate if 'status' string is a valid SubmissionStatus
        // com.ufomap.api.model.SubmissionStatus.fromString(status); // This would throw IllegalArgumentException if invalid

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
                .id(dto.getId()) // ID might be null for new entities
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