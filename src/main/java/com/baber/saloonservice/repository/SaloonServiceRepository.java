package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.SaloonServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaloonServiceRepository extends JpaRepository<SaloonServices, Long> {

    // Find services by saloon ID
    @Query("SELECT s FROM SaloonServices s WHERE s.saloon.id = :saloonId")
    List<SaloonServices> findBySaloonId(@Param("saloonId") Long saloonId);
}
