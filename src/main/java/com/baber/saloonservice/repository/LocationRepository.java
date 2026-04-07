package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    // Find locations by city
    List<Location> findByCity(String city);
    
    // Find locations by state
    List<Location> findByState(String state);
    
    // Find locations by country
    List<Location> findByCountry(String country);
    
    // Find locations by name containing (case-insensitive)
    List<Location> findByNameContainingIgnoreCase(String name);
    
    // Find locations by address containing (case-insensitive)
    List<Location> findByAddressContainingIgnoreCase(String address);
    
    // Custom query to find locations within a certain distance
    @Query(value = "SELECT l.* FROM t_location l " +
                   "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(CAST(l.latitude AS DECIMAL))) * " +
                   "cos(radians(CAST(l.longitude AS DECIMAL)) - radians(:longitude)) + " +
                   "sin(radians(:latitude)) * sin(radians(CAST(l.latitude AS DECIMAL))))) <= :distance", 
           nativeQuery = true)
    List<Location> findLocationsWithinDistance(@Param("latitude") double latitude, 
                                             @Param("longitude") double longitude, 
                                             @Param("distance") double distance);
}
