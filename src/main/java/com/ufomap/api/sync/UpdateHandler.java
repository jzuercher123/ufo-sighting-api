package com.ufomap.api.sync;

import com.ufomap.api.model.Sighting; // Assuming Sighting model exists
import com.ufomap.api.service.SightingService; // To interact with Sighting data
import com.ufomap.api.exception.ResourceNotFoundException; // For handling missing sightings
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // For UpdateSource

// Records defined in the previous step (assuming they are in the same package or imported)
/*
record SightingUpdateData(
    Optional<String> city,
    Optional<String> state,
    // ... other fields
    Optional<String> submissionStatus
) {}

record UpdateSource(
    String sourceSystem,
    Optional<String> updatedByUserId,
    LocalDateTime updateTimestamp
) {}

interface Update<T, ID> {
    ID getTargetId();
    T getDataPayload();
    UpdateSource getSourceInfo();
}

class SightingUpdateEvent implements Update<SightingUpdateData, Long> {
    // ... constructor and methods
}
*/


@Service // Marks this as a Spring service component
public class UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateHandler.class);

    private final SightingService sightingService;
    // You might also inject a mapper if conversions are complex,
    // but for direct field setting, it might not be needed.

    // Constructor injection for SightingService
    public UpdateHandler(SightingService sightingService) {
        this.sightingService = sightingService;
    }

    /**
     * Handles a generic sighting update event.
     * This method is transactional to ensure atomicity of the update.
     *
     * @param updateEvent The event containing update details for a sighting.
     * @throws ResourceNotFoundException if the sighting to update is not found.
     */
    @Transactional // Ensures the read and write operations are part of a single transaction
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
            // Optionally throw an IllegalArgumentException
            return;
        }

        logger.info("Processing update for sighting ID: {} from source: {}. Timestamp: {}",
                sightingId,
                sourceInfo != null ? sourceInfo.sourceSystem() : "Unknown",
                sourceInfo != null ? sourceInfo.updateTimestamp() : "N/A");

        // Fetch the existing sighting. SightingService.getSightingById should handle not found.
        // Assuming getSightingById returns the Sighting entity or throws ResourceNotFoundException
        Sighting sightingToUpdate = sightingService.getSightingById(sightingId)
                .orElseThrow(() -> new ResourceNotFoundException("Sighting not found with id: " + sightingId)); //

        // Apply updates from the payload
        // For each field in SightingUpdateData, check if it's present and update the entity.
        payload.city().ifPresent(sightingToUpdate::setCity);
        payload.state().ifPresent(sightingToUpdate::setState);
        payload.country().ifPresent(sightingToUpdate::setCountry);
        payload.shape().ifPresent(sightingToUpdate::setShape);
        payload.duration().ifPresent(sightingToUpdate::setDuration);
        payload.summary().ifPresent(sightingToUpdate::setSummary);
        payload.latitude().ifPresent(sightingToUpdate::setLatitude);
        payload.longitude().ifPresent(sightingToUpdate::setLongitude);
        payload.submissionStatus().ifPresent(sightingToUpdate::setSubmissionStatus);
        // Potentially update a "lastModifiedDate" or similar audit field on Sighting entity
        // sightingToUpdate.setLastModifiedDate(LocalDateTime.now());
        // sightingToUpdate.setLastModifiedBy(sourceInfo.updatedByUserId().orElse("sync-process"));


        // Save the updated sighting
        // Assuming SightingService has an update method or save method that handles updates.
        // If SightingService.updateSighting takes DTO, you might need to map Sighting entity back to DTO,
        // or preferably, have SightingService.updateSighting accept the entity or necessary fields.
        // For simplicity, let's assume SightingRepository (used by SightingService) handles the save.
        sightingService.updateSighting(sightingId, convertToDTO(sightingToUpdate)); // This assumes SightingService.updateSighting takes a DTO
        // or sightingService.save(sightingToUpdate) if it takes entity

        logger.info("Successfully updated sighting ID: {}. Details: {}", sightingId, payload);

        // Additional post-update logic can go here (e.g., sending notifications, logging to audit trails)
    }

    // Helper method to convert Sighting entity to SightingDTO if needed by SightingService
    // This is a basic example; you'd use a proper mapper (e.g., MapStruct) in a real app.
    private SightingDTO convertToDTO(Sighting sighting) {
        SightingDTO dto = new SightingDTO();
        dto.setId(sighting.getId());
        dto.setDateTime(sighting.getDateTime());
        dto.setCity(sighting.getCity());
        dto.setState(sighting.getState());
        dto.setCountry(sighting.getCountry());
        dto.setShape(sighting.getShape());
        dto.setDuration(sighting.getDuration());
        dto.setSummary(sighting.getSummary());
        dto.setPosted(sighting.getPosted());
        dto.setLatitude(sighting.getLatitude());
        dto.setLongitude(sighting.getLongitude());
        dto.setSubmittedBy(sighting.getSubmittedBy());
        dto.setSubmissionDate(sighting.getSubmissionDate());
        dto.setUserSubmitted(sighting.isUserSubmitted()); // Ensure field name matches DTO if different
        dto.setSubmissionStatus(sighting.getSubmissionStatus());
        return dto;
    }
}