package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.SaloonServiceType;
import com.baber.saloonservice.model.SaloonServices;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaloonServiceTypeRepository extends JpaRepository<SaloonServiceType, Long> {

}
