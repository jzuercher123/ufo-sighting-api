package com.ufomap.api.sync;

import java.time.LocalDateTime;
import java.util.Optional; // For optional update fields

// Renamed from Data to avoid collision with java.util.Date if ever used, and to be more specific.
// This record holds the fields that can be updated for a Sighting.
// Use Optional<T> for fields that are not always part of an update.
record SightingUpdateData(
        Optional<String> city,
        Optional<String> state,
        Optional<String> country,
        Optional<String> shape,
        Optional<String> duration,
        Optional<String> summary,
        Optional<Double> latitude,
        Optional<Double> longitude,
        Optional<String> submissionStatus // e.g., "approved", "rejected"
        // Add other fields from Sighting.java or SightingDTO.java that are updatable
        // For example, you might not want to allow direct updates to 'id', 'submittedBy', or 'submissionDate' via this mechanism.
) {
    // You can add a constructor or factory methods if needed for more complex creation logic.
}

// This record can hold information about the source or context of the update.
record UpdateSource(
        String sourceSystem, // e.g., "admin-portal", "external-feed", "user-correction"
        Optional<String> updatedByUserId, // ID of the user performing the update, if applicable
        LocalDateTime updateTimestamp
) {}

// The Update interface now defines a contract for an update operation or data structure.
// This version treats Update as a container for the data to be updated and its source.
public interface Update<T, ID> { // T is the type of data, ID is the type of the identifier for the entity to update

    /**
     * Gets the unique identifier of the entity to be updated.
     * @return The ID of the entity.
     */
    ID getTargetId();

    /**
     * Gets the data payload containing the fields to be updated.
     * @return The update data.
     */
    T getDataPayload();

    /**
     * Gets information about the source of this update.
     * @return The update source metadata.
     */
    UpdateSource getSourceInfo();

    /**
     * (Optional Method Example)
     * A method to apply this update to a target object.
     * This is just an example; you might handle the update logic in a service class.
     *
     * @param target The object to apply the update to.
     * @return The updated object, or a new instance with updates applied.
     */
    // S applyTo(S target); // If Sighting is the target type
}

// Example implementation of the Update interface for Sighting updates:
class SightingUpdateEvent implements Update<SightingUpdateData, Long> {
    private final Long sightingId;
    private final SightingUpdateData payload;
    private final UpdateSource sourceInfo;

    public SightingUpdateEvent(Long sightingId, SightingUpdateData payload, UpdateSource sourceInfo) {
        this.sightingId = sightingId;
        this.payload = payload;
        this.sourceInfo = sourceInfo;
    }

    @Override
    public Long getTargetId() {
        return sightingId;
    }

    @Override
    public SightingUpdateData getDataPayload() {
        return payload;
    }

    @Override
    public UpdateSource getSourceInfo() {
        return sourceInfo;
    }

    // Example usage (conceptual):
    // public Sighting applyTo(Sighting target) {
    //     if (target == null || !target.getId().equals(this.sightingId)) {
    //         throw new IllegalArgumentException("Target sighting ID does not match.");
    //     }
    //     payload.city().ifPresent(target::setCity);
    //     payload.state().ifPresent(target::setState);
    //     // ... apply other fields
    //     return target;
    // }
}