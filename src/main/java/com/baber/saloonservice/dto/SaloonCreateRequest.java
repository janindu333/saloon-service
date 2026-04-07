package com.baber.saloonservice.dto;

import java.util.List;

import jakarta.annotation.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaloonCreateRequest {
    private String name;
    private String address;
    private String phoneNumber;
    private String description;
    private String openingDays;
    private String openTime;
    private String closeTime;
    private String latitude;
    private String longitude; 
}