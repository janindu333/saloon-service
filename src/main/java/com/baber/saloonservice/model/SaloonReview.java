package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_saloon_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonReview {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String customerImageUrl;
    private String reviewDesc;
    private String reviewAddedTime;
}
