package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.SaloonSpecialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaloonSpecialistRepository extends JpaRepository<SaloonSpecialist, Long> {
    
    // Find specialists by service ID (through saloon services)
    @Query("SELECT DISTINCT ss FROM SaloonSpecialist ss " +
           "JOIN ss.saloons s " +
           "JOIN s.saloonServics srv " +
           "WHERE srv.id = :serviceId")
    List<SaloonSpecialist> findByServiceId(@Param("serviceId") Long serviceId);
    
    // Find specialists by name (case-insensitive)
    List<SaloonSpecialist> findByNameContainingIgnoreCase(String name);
    
    // Find specialists by position
    List<SaloonSpecialist> findByPossition(String position);
    
    // Find specialists by saloon ID
    @Query("SELECT ss FROM SaloonSpecialist ss JOIN ss.saloons s WHERE s.id = :saloonId")
    List<SaloonSpecialist> findBySaloonId(@Param("saloonId") Long saloonId);
}
