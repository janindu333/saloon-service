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
public class SaloonSpecialistHairStyleImages extends Base{

    private String saloonSpecialistImageUrl;

    @ManyToOne
    @JoinColumn(name = "saloon_specialist_id")
    private SaloonSpecialist saloonSpecialist;

}
