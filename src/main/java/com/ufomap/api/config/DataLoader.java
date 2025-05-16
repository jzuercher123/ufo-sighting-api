package com.ufomap.api.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufomap.api.dto.SightingDTO;
import com.ufomap.api.model.Sighting;
import com.ufomap.api.repository.SightingRepository;
import com.ufomap.api.service.SightingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Profile("!prod") // Don't run in production
// Load sighting DATA from database
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final SightingRepository sightingRepository;

    private final SightingService sightingService;


    @Value("classpath:data/sightings.json")
    private Resource sightingsResource;

    @PostConstruct
    public void loadData() {
        if (sightingRepository.count() > 0) {
            logger.info("Database already contains data, skipping data load");
            return;
        }

        logger.info("Loading initial sightings data from JSON...");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            List<Map<String, Object>> sightingsData = objectMapper.readValue(
                    sightingsResource.getInputStream(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            logger.info("Found {} sighting records in JSON file", sightingsData.size());

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            List<Sighting> sightings = sightingsData.stream()
                    .map(data -> {
                        Sighting sighting = new Sighting();

                        // Handle potential nulls and cast errors
                        try {
                            if (data.get("dateTime") != null) {
                                String dateTimeStr = data.get("dateTime").toString();
                                sighting.setDateTime(LocalDateTime.parse(dateTimeStr, dateFormatter));
                            }

                            sighting.setCity(data.get("city") != null ? data.get("city").toString() : null);
                            sighting.setState(data.get("state") != null ? data.get("state").toString() : null);
                            sighting.setCountry(data.get("country") != null ? data.get("country").toString() : null);
                            sighting.setShape(data.get("shape") != null ? data.get("shape").toString() : null);
                            sighting.setDuration(data.get("duration") != null ? data.get("duration").toString() : null);
                            sighting.setSummary(data.get("summary") != null ? data.get("summary").toString() : null);
                            sighting.setPosted(data.get("posted") != null ? data.get("posted").toString() : null);

                            if (data.get("latitude") != null) {
                                sighting.setLatitude(Double.parseDouble(data.get("latitude").toString()));
                            }

                            if (data.get("longitude") != null) {
                                sighting.setLongitude(Double.parseDouble(data.get("longitude").toString()));
                            }

                            sighting.setSubmittedBy(data.get("submittedBy") != null ? data.get("submittedBy").toString() : null);

                            if (data.get("submissionDate") != null) {
                                String submissionDateStr = data.get("submissionDate").toString();
                                sighting.setSubmissionDate(LocalDateTime.parse(submissionDateStr, dateFormatter));
                            }

                            if (data.get("isUserSubmitted") != null) {
                                sighting.setUserSubmitted(Boolean.parseBoolean(data.get("isUserSubmitted").toString()));
                            } else {
                                sighting.setUserSubmitted(false);
                            }

                            sighting.setSubmissionStatus(
                                    data.get("submissionStatus") != null ?
                                            data.get("submissionStatus").toString() :
                                            "approved"
                            );

                            return sighting;
                        } catch (Exception e) {
                            logger.error("Error parsing sighting data: {}", e.getMessage());
                            return null;
                        }
                    })
                    .filter(s -> s != null && s.getLatitude() != null && s.getLongitude() != null)
                    .toList();

            logger.info("Successfully parsed {} valid sightings", sightings.size());
            sightingRepository.saveAll(sightings);
            logger.info("Successfully loaded initial sightings data into the database");

        } catch (IOException e) {
            logger.error("Failed to load initial sightings data: {}", e.getMessage(), e);
        }
    }


}