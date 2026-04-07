package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.SaloonCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaloonCategoryRepository extends JpaRepository<SaloonCategory, Long> {

    // Find category by name (case-insensitive)
    List<SaloonCategory> findByNameContainingIgnoreCase(String name);
    
    // Find category by exact name
    SaloonCategory findByName(String name);
    
    // Check if category exists by name
    boolean existsByName(String name);
}
