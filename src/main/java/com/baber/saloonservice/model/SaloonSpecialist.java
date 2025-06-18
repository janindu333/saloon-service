package com.baber.saloonservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
@Entity
@Table(name = "t_saloon_specialist")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaloonSpecialist extends Base{
    private String name;
    private String possition;
    private String image;
    private String number;
    private String about;

    @ManyToMany(mappedBy = "specialists")
    @JsonIgnoreProperties("specialists")
    private List<Saloon> saloons = new ArrayList<>();
    @OneToMany(mappedBy = "saloonSpecialist")
    private Set<SaloonSpecialistHairStyleImages> hairstyleImages = new HashSet<>();
    @OneToMany(mappedBy = "saloonSpecialist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilitySlot> availabilitySlots = new ArrayList<>();

}
