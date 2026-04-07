package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_location")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location extends Base {
    
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String latitude;
    private String longitude;
    private String description;
    
    // Additional location metadata
    private String timezone;
    private String formattedAddress;
    private String placeId; // Google Places ID if using Google Maps API
} 