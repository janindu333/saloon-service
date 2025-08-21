package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.Saloon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

public interface SaloonRepository extends JpaRepository<Saloon, Long> {

    @Query("SELECT s.id FROM Saloon s JOIN s.specialists sp WHERE sp.id = :specialistId")
    Set<Long> findSaloonIdsBySpecialistId(@Param("specialistId") Long specialistId);
    
    // Find saloons by category
    @Query("SELECT DISTINCT s FROM Saloon s JOIN s.categories c WHERE c.id = :categoryId")
    List<Saloon> findByCategoryId(@Param("categoryId") Long categoryId);
    
    // Search saloons by name, address, description, or phone number
    @Query("SELECT s FROM Saloon s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.address) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Saloon> searchSaloons(@Param("query") String query);
    
    // Find saloons by location (primary or secondary)
    @Query("SELECT DISTINCT s FROM Saloon s " +
           "LEFT JOIN s.locations l " +
           "WHERE s.primaryLocation.id = :locationId OR l.id = :locationId")
    List<Saloon> findByLocationId(@Param("locationId") Long locationId);
}

