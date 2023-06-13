package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_saloon_worker_hair_style_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonSpecialistHairStyleImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSaloonSpecialistImage;
    private String saloonSpecialistImageUrl;
}
