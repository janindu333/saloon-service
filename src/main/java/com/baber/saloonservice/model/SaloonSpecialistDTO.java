package com.baber.saloonservice.model;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SaloonSpecialistDTO {
    private Long id;
     private String name;
    private String possition;
    private String image;
    private String number;
    private String about;
    private Set<Long> saloonIds;
    private Set<Long> idSaloonSpecialistImages;

    public SaloonSpecialistDTO(Long id, Set<Long> saloonIds, Set<Long> idSaloonSpecialistImages) {
        this.id = id;
        this.saloonIds = saloonIds;
        this.idSaloonSpecialistImages =idSaloonSpecialistImages;
    }
  
}
