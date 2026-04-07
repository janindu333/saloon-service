package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.PaymentSettings;
import com.baber.saloonservice.model.Saloon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentSettingsRepository extends JpaRepository<PaymentSettings, Long> {
    
    Optional<PaymentSettings> findBySalon(Saloon salon);
    
    Optional<PaymentSettings> findBySalonId(Long salonId);
}
