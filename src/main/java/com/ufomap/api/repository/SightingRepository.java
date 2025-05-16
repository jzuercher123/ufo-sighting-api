package com.ufomap.api.repository;

import com.ufomap.api.model.Sighting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SightingRepository extends JpaRepository<Sighting, Long> {

    List<Sighting> findByCountry(String country);

    List<Sighting> findByState(String state);

    List<Sighting> findByCity(String city);

    List<Sighting> findByShape(String shape);

    List<Sighting> findBySubmissionStatus(String status);

    List<Sighting> findBySubmittedBy(String submittedBy);

    @Query("SELECT s FROM Sighting s WHERE " +
            "(:shape IS NULL OR s.shape = :shape) AND " +
            "(:city IS NULL OR s.city = :city) AND " +
            "(:country IS NULL OR s.country = :country) AND " +
            "(:state IS NULL OR s.state = :state) AND " +
            "(:searchText IS NULL OR " +
            "s.city LIKE %:searchText% OR " +
            "s.state LIKE %:searchText% OR " +
            "s.country LIKE %:searchText% OR " +
            "s.summary LIKE %:searchText% OR " +
            "s.shape LIKE %:searchText%)")
    List<Sighting> findWithFilters(
            @Param("shape") String shape,
            @Param("city") String city,
            @Param("country") String country,
            @Param("state") String state,
            @Param("searchText") String searchText
    );

    @Query("SELECT s FROM Sighting s WHERE " +
            "s.latitude BETWEEN :south AND :north AND " +
            "s.longitude BETWEEN :west AND :east")
    List<Sighting> findInBounds(
            @Param("north") Double north,
            @Param("south") Double south,
            @Param("east") Double east,
            @Param("west") Double west
    );
}