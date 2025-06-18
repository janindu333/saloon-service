package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.LocationWithSaloonId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationWithSaloonId, Long> {
    
}
