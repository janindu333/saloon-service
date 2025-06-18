package com.baber.saloonservice.repository;

import org.springframework.data.jpa.repository.JpaRepository; 
import com.baber.saloonservice.model.SaloonSpecialist;

public interface SaloonSpecialistRepository extends JpaRepository<SaloonSpecialist, Long> {
    
}
