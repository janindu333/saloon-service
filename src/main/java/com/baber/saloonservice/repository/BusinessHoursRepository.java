package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.BusinessHours;
import com.baber.saloonservice.model.Saloon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {
    
    List<BusinessHours> findBySalon(Saloon salon);
    
    @Query("SELECT bh FROM BusinessHours bh WHERE bh.salon.id = :salonId")
    List<BusinessHours> findBySalonId(@Param("salonId") Long salonId);
    
    Optional<BusinessHours> findBySalonAndDayOfWeek(Saloon salon, String dayOfWeek);
    
    @Modifying
    @Query("DELETE FROM BusinessHours bh WHERE bh.salon.id = :salonId")
    void deleteBySalonId(@Param("salonId") Long salonId);
}
