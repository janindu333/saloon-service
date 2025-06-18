package com.baber.saloonservice.controller;

import com.baber.saloonservice.dto.BaseResponse;
import com.baber.saloonservice.dto.SaloonReviewRequest;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.model.SaloonReview;
import com.baber.saloonservice.service.SaloonReviewService;
import com.baber.saloonservice.service.SaloonService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saloon/review")
public class SaloonReviewController {

    private final SaloonReviewService saloonReviewService;

    public SaloonReviewController(SaloonReviewService saloonReviewService) {
        this.saloonReviewService = saloonReviewService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<String> createSaloon(@RequestBody SaloonReviewRequest saloonReviewRequest) {

        try {
            saloonReviewService.createSaloon(saloonReviewRequest);
            return new BaseResponse<>(true, "Success", 0, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure " + e.getMessage(), 1, "", null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deleteSaloon(@PathVariable Long id) {
        try {
            // Call the service method to delete the Saloon by id
            saloonReviewService.deleteSaloonReviewById(id);
            return new BaseResponse<>(true, "Success", 0, "", null);
        } catch (EmptyResultDataAccessException e) {
            // The specified id doesn't exist in the database
            return new BaseResponse<>(false,null , 0, "Review not available", null);
        } catch (Exception e) {
            // Other error occurred
            return new BaseResponse<>(false, null, 0, "Failed to delete, please try again later", null);
        }
    }
}
