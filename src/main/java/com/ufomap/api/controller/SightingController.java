package com.ufomap.api.controller;

import com.ufomap.api.dto.SightingDTO;
import com.ufomap.api.model.SubmissionStatus; // Import your enum
import com.ufomap.api.service.SightingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Correct import for Pageable
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sightings")
@RequiredArgsConstructor
public class SightingController {

    private final SightingService sightingService;

    /**
     * Retrieves all sightings with pagination.
     * @param pageable Pagination information (page, size, sort).
     * @return A page of SightingDTOs.
     */
    @GetMapping
    public ResponseEntity<Page<SightingDTO>> getAllSightings(Pageable pageable) {
        return ResponseEntity.ok(sightingService.getAllSightings(pageable));
    }

    /**
     * Retrieves a specific sighting by its ID.
     * @param id The ID of the sighting.
     * @return The SightingDTO if found, or 404 if not.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SightingDTO> getSightingById(@PathVariable Long id) {
        return ResponseEntity.ok(sightingService.getSightingById(id));
    }

    /**
     * Retrieves sightings based on various filter criteria with pagination.
     * @param shape Optional shape filter.
     * @param city Optional city filter.
     * @param country Optional country filter.
     * @param state Optional state filter.
     * @param searchText Optional free-text search across multiple fields.
     * @param pageable Pagination information.
     * @return A page of SightingDTOs matching the filters.
     */
    @GetMapping("/filter")
    public ResponseEntity<Page<SightingDTO>> getSightingsWithFilters(
            @RequestParam(required = false) String shape,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String searchText,
            Pageable pageable) {
        return ResponseEntity.ok(sightingService.getSightingsWithFilters(
                shape, city, country, state, searchText, pageable));
    }

    /**
     * Retrieves sightings within a given geographical bounding box with pagination.
     * @param north The northern latitude boundary.
     * @param south The southern latitude boundary.
     * @param east The eastern longitude boundary.
     * @param west The western longitude boundary.
     * @param pageable Pagination information.
     * @return A page of SightingDTOs within the bounds.
     */
    @GetMapping("/bounds")
    public ResponseEntity<Page<SightingDTO>> getSightingsInBounds(
            @RequestParam Double north,
            @RequestParam Double south,
            @RequestParam Double east,
            @RequestParam Double west,
            Pageable pageable) {
        return ResponseEntity.ok(sightingService.getSightingsInBounds(north, south, east, west, pageable));
    }

    /**
     * Creates a new sighting.
     * @param sightingDTO The SightingDTO to create.
     * @return The created SightingDTO with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<SightingDTO> createSighting(@Valid @RequestBody SightingDTO sightingDTO) {
        SightingDTO createdSighting = sightingService.createSighting(sightingDTO);
        return new ResponseEntity<>(createdSighting, HttpStatus.CREATED);
    }

    /**
     * Updates the submission status of a specific sighting.
     * @param id The ID of the sighting to update.
     * @param status The new submission status.
     * @return The updated SightingDTO.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<SightingDTO> updateSightingStatus(
            @PathVariable Long id,
            @RequestParam SubmissionStatus status) { // Use the enum type directly
        // Assuming SightingService.updateSightingStatus expects the enum or its string representation
        return ResponseEntity.ok(sightingService.updateSightingStatus(id, status.getStatus())); // or status.toString() or just status if service expects enum
    }
}