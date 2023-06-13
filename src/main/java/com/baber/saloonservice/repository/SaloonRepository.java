package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.Saloon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaloonRepository extends JpaRepository<Saloon, Long> {

//    @Query("SELECT s FROM Saloon s JOIN s.categories c WHERE c.id = :categoryId")
//    List<Saloon> findByCategoryId(Long categoryId);
}
