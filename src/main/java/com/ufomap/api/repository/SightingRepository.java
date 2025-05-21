package com.ufomap.api.repository;

import com.ufomap.api.model.Sighting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SightingRepository extends JpaRepository<Sighting, Long> {

    // Simple finders - now case-insensitive
    List<Sighting> findByCountryIgnoreCase(String country);
    Page<Sighting> findByCountryIgnoreCase(String country, Pageable pageable);

    List<Sighting> findByStateIgnoreCase(String state);
    Page<Sighting> findByStateIgnoreCase(String state, Pageable pageable);

    List<Sighting> findByCityIgnoreCase(String city);
    Page<Sighting> findByCityIgnoreCase(String city, Pageable pageable);

    List<Sighting> findByShapeIgnoreCase(String shape);
    Page<Sighting> findByShapeIgnoreCase(String shape, Pageable pageable);

    List<Sighting> findBySubmissionStatusIgnoreCase(String status);
    Page<Sighting> findBySubmissionStatusIgnoreCase(String status, Pageable pageable);

    List<Sighting> findBySubmittedByIgnoreCase(String submittedBy);
    Page<Sighting> findBySubmittedByIgnoreCase(String submittedBy, Pageable pageable);


    /**
     * Finds sightings based on various filter criteria.
     * All textual comparisons for specific fields (shape, city, country, state) are case-insensitive.
     * The searchText performs a case-insensitive "contains" search across multiple fields.
     *
     * @param shape      Optional sighting shape (case-insensitive).
     * @param city       Optional city name (case-insensitive).
     * @param country    Optional country name (case-insensitive).
     * @param state      Optional state name (case-insensitive).
     * @param searchText Optional text to search within city, state, country, summary, or shape (case-insensitive).
     * @param pageable   Pagination information.
     * @return A Page of Sighting objects matching the criteria.
     */
    @Query("SELECT s FROM Sighting s WHERE " +
            "(:shape IS NULL OR LOWER(s.shape) = LOWER(:shape)) AND " +
            "(:city IS NULL OR LOWER(s.city) = LOWER(:city)) AND " +
            "(:country IS NULL OR LOWER(s.country) = LOWER(:country)) AND " +
            "(:state IS NULL OR LOWER(s.state) = LOWER(:state)) AND " +
            "(:searchText IS NULL OR (" +
            "LOWER(s.city) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.state) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.country) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.summary) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.shape) LIKE LOWER(CONCAT('%', :searchText, '%'))" +
            "))")
    Page<Sighting> findWithFilters(
            @Param("shape") String shape,
            @Param("city") String city,
            @Param("country") String country,
            @Param("state") String state,
            @Param("searchText") String searchText,
            Pageable pageable // Added Pageable
    );

    /**
     * Finds sightings within a given geographical bounding box.
     *
     * @param north The northern latitude boundary.
     * @param south The southern latitude boundary.
     * @param east  The eastern longitude boundary.
     * @param west  The western longitude boundary.
     * @param pageable Pagination information.
     * @return A Page of Sighting objects within the bounds.
     */
    @Query("SELECT s FROM Sighting s WHERE " +
            "s.latitude BETWEEN :south AND :north AND " +
            "s.longitude BETWEEN :west AND :east")
    Page<Sighting> findInBounds(
            @Param("north") Double north,
            @Param("south") Double south,
            @Param("east") Double east,
            @Param("west") Double west,
            Pageable pageable // Added Pageable
    );
}