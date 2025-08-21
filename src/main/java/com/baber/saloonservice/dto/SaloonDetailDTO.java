package com.baber.saloonservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

import com.baber.saloonservice.dto.SaloonSpecialistDTO; 


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaloonDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String address;
    private double rating;
    private int noOfReviews;
    private String imageUrl;
    private boolean userLiked;
    private String phoneNumber;
    private String description;
    private String openingDays;
    private String openTime;
    private String closeTime;
    private String location;
    private String latitude;
    private String longitude;
    
    // Related entities
    private List<OfferItemsDto> offers;
    private List<SaloonCategoryDTO> categories;
    private List<SaloonSpecialistDTO> specialists;
    private List<SaloonReviewDTO> reviews;
    private List<SaloonGalleryDTO> galleries;
    private List<SaloonServicesDTO> services;
} 