package com.baber.saloonservice.service;

import com.baber.saloonservice.dto.SaloonReviewRequest;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.model.SaloonReview;
import com.baber.saloonservice.repository.SaloonRepository;
import com.baber.saloonservice.repository.SaloonReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class SaloonReviewService {

    private final SaloonReviewRepository saloonReviewRepository;
    private final SaloonRepository saloonRepository;

    public SaloonReviewService(SaloonReviewRepository saloonReviewRepository , SaloonRepository saloonRepository) {
        this.saloonReviewRepository = saloonReviewRepository;
        this.saloonRepository = saloonRepository;
    }

    public void createSaloon(SaloonReviewRequest saloonReviewRequest) {

        SaloonReview saloonReview = new SaloonReview();
        saloonReview.setReviewDesc(saloonReviewRequest.getReviewDescription());
        saloonReview.setReviewAddedTime(LocalDateTime.now());
        saloonReview.setRating(saloonReviewRequest.getRating());
        // Set the associated Saloon
        Saloon saloon = saloonRepository.findById(saloonReviewRequest.getSaloonId()).orElseThrow(() ->
                new EntityNotFoundException("Saloon not found"));
        saloonReview.setSaloon(saloon);
        saloonReviewRepository.save(saloonReview);
    }

    public void deleteSaloonReviewById(Long id) {
        saloonReviewRepository.deleteById(id);
    }

}
