package com.baber.saloonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id; // Internal ID (kept for backward compatibility)
    private java.util.UUID publicId; // UUID for external APIs
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
    
    // Location information
    private LocationDTO primaryLocation;
    private List<LocationDTO> locations;
    
    // Categories
    private List<SaloonCategoryDTO> categories;
    
    // Services
    private List<SaloonServicesDTO> services;
    
    // Reviews
    private List<SaloonReviewDTO> reviews;
    
    // Specialists
    private List<SaloonSpecialistDTO> specialists;
    
    // Offers
    private List<OfferItemsDto> offers;
    
    // Gallery
    private List<SaloonGalleryDTO> gallery;
}
