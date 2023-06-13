package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "t_saloon_specialist")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaloonSpecialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String possition;
    private String image;
    private String number;
    private String about;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SaloonSpecialistHairStyleImages> hairStyleImages;

}
