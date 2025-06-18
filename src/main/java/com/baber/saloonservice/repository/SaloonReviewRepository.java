package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.model.SaloonReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface SaloonReviewRepository extends JpaRepository<SaloonReview, Long> {



}
