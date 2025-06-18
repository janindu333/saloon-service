package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.SaloonServices;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaloonServiceRepository extends JpaRepository<SaloonServices, Long> {

}
