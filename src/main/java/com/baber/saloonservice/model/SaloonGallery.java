package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_saloon_gallery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonGallery extends Base{
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "saloon_id")
    private Saloon saloon;
}
