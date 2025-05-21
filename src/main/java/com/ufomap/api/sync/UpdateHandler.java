package com.ufomap.api.sync;

import com.ufomap.api.dto.SightingDTO; // For the convertToDTO helper, if SightingService.updateSighting expects DTO
import com.ufomap.api.model.Sighting;
import com.ufomap.api.service.SightingService;
import com.ufomap.api.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Records SightingUpdateData and UpdateSource are assumed to be in this package or imported
// Interface Update and class SightingUpdateEvent are assumed to be in this package or imported


@Service
public class UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateHandler.class);

    private final SightingService sightingService;

    public UpdateHandler(SightingService sightingService) {
        this.sightingService = sightingService;
    }

    @Transactional
    public void handleSightingUpdate(Update<SightingUpdateData, Long> updateEvent) {
        if (updateEvent == null) {
            logger.warn("Received null update event.");
            return;
        }

        Long sightingId = updateEvent.getTargetId();
        SightingUpdateData payload = updateEvent.getDataPayload();
        UpdateSource sourceInfo = updateEvent.getSourceInfo();

        if (sightingId == null || payload == null) {
            logger.error("Update event is missing sightingId or payload. Source: {}",
                    sourceInfo != null ? sourceInfo.sourceSystem() : "Unknown");
            return;
        }

        logger.info("Processing update for sighting ID: {} from source: {}. Timestamp: {}",
                sightingId,
                sourceInfo != null ? sourceInfo.sourceSystem() : "Unknown",
                sourceInfo != null ? sourceInfo.updateTimestamp() : "N/A");

        // Fetch the existing sighting entity
        Sighting sightingToUpdate = sightingService.getSightingEntityById(sightingId);

        // Apply updates from the payload
        payload.city().ifPresent(sightingToUpdate::setCity);
        payload.state().ifPresent(sightingToUpdate::setState);
        payload.country().ifPresent(sightingToUpdate::setCountry);
        payload.shape().ifPresent(sightingToUpdate::setShape);
        payload.duration().ifPresent(sightingToUpdate::setDuration);
        payload.summary().ifPresent(sightingToUpdate::setSummary);
        payload.latitude().ifPresent(sightingToUpdate::setLatitude);
        payload.longitude().ifPresent(sightingToUpdate::setLongitude);

        // If submissionStatus is part of SightingUpdateData and should be updatable here:
        payload.submissionStatus().ifPresent(status -> {
            // You might want to use the dedicated service method for status updates
            // to keep logic consistent, or update directly if appropriate.
            // For direct update:
            sightingToUpdate.setSubmissionStatus(status);
            // Or call:
            // sightingService.updateSightingStatus(sightingId, status);
            // However, the current flow will save sightingToUpdate which includes this change.
        });
        // Potentially update a "lastModifiedDate" or similar audit field on Sighting entity
        // sightingToUpdate.setLastModifiedDate(LocalDateTime.now());
        // sightingToUpdate.setLastModifiedBy(sourceInfo.updatedByUserId().orElse("sync-process"));

        // Save the updated sighting using the Sighting entity itself
        // We need a service method that accepts the entity or updates based on DTO like we added.
        // Let's use the SightingService.updateSighting(Long id, SightingDTO dto) method.
        // This requires converting the modified entity back to DTO or enhancing SightingService.
        // For simplicity with the added SightingService.updateSighting:
        SightingDTO updatedSightingDTO = convertToDTO(sightingToUpdate); // Convert the *modified* entity
        sightingService.updateSighting(sightingId, updatedSightingDTO); // Call the new service method

        logger.info("Successfully updated sighting ID: {}. Details: {}", sightingId, payload);
    }

    // Helper method to convert Sighting entity to SightingDTO
    // This should be consistent with the one in SightingService or use a shared mapper
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
}