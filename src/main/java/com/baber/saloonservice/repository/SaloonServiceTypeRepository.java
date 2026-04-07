package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.SaloonServiceType;
import com.baber.saloonservice.model.SaloonServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaloonServiceTypeRepository extends JpaRepository<SaloonServiceType, Long> {

    // Find service types by saloon service ID
    @Query("SELECT st FROM SaloonServiceType st WHERE st.saloonService.id = :serviceId")
    List<SaloonServiceType> findBySaloonServiceId(@Param("serviceId") Long serviceId);
}
