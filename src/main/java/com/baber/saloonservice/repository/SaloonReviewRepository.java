package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.model.SaloonReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SaloonReviewRepository extends JpaRepository<SaloonReview, Long> {

    // Find reviews by saloon ID
    @Query("SELECT r FROM SaloonReview r WHERE r.saloon.id = :saloonId ORDER BY r.reviewAddedTime DESC")
    List<SaloonReview> findBySaloonId(@Param("saloonId") Long saloonId);
    
    // Find reviews by rating
    List<SaloonReview> findByRating(String rating);
    
    // Find reviews by saloon ID and rating
    @Query("SELECT r FROM SaloonReview r WHERE r.saloon.id = :saloonId AND r.rating = :rating ORDER BY r.reviewAddedTime DESC")
    List<SaloonReview> findBySaloonIdAndRating(@Param("saloonId") Long saloonId, @Param("rating") String rating);
}
