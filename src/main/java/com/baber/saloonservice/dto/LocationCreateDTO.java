package com.baber.saloonservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationCreateDTO {
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String latitude;
    private String longitude;
    private String description;
    private String timezone;
    private String formattedAddress;
    private String placeId;
} 