package com.baber.saloonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonSpecialistDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String possition;
    private String image;
    private String number;
    private String about;
    
    // Associated saloon IDs
    private Set<Long> saloonIds;
    
    // Associated hairstyle image IDs
    private Set<Long> hairstyleImageIds;
} 