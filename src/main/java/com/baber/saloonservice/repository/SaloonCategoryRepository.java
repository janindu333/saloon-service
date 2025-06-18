package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.SaloonCategory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SaloonCategoryRepository extends JpaRepository<SaloonCategory, Long> {

//    @Query("SELECT c FROM SaloonCategory c JOIN c.saloons s WHERE s.id = :saloonId")
//    List<SaloonCategory> findBySaloonId(Long saloonId);

}
