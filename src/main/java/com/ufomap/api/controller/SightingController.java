package com.ufomap.api.controller;

import com.ufomap.api.dto.SightingDTO;
import com.ufomap.api.service.SightingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sightings")
@RequiredArgsConstructor
public class SightingController {

    private final SightingService sightingService;

    @GetMapping
    public ResponseEntity<List<SightingDTO>> getAllSightings() {
        return ResponseEntity.ok(sightingService.getAllSightings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SightingDTO> getSightingById(@PathVariable Long id) {
        return ResponseEntity.ok(sightingService.getSightingById(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SightingDTO>> getSightingsWithFilters(
            @RequestParam(required = false) String shape,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String searchText) {

        return ResponseEntity.ok(sightingService.getSightingsWithFilters(
                shape, city, country, state, searchText));
    }

    @GetMapping("/bounds")
    public ResponseEntity<List<SightingDTO>> getSightingsInBounds(
            @RequestParam Double north,
            @RequestParam Double south,
            @RequestParam Double east,
            @RequestParam Double west) {

        return ResponseEntity.ok(sightingService.getSightingsInBounds(north, south, east, west));
    }

    @PostMapping
    public ResponseEntity<SightingDTO> createSighting(@Valid @RequestBody SightingDTO sightingDTO) {
        SightingDTO createdSighting = sightingService.createSighting(sightingDTO);
        return new ResponseEntity<>(createdSighting, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SightingDTO> updateSightingStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(sightingService.updateSightingStatus(id, status));
    }
}