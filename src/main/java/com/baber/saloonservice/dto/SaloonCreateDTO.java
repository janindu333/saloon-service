package com.baber.saloonservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonCreateDTO {
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
    private LocationDTO primaryLocation;
} 