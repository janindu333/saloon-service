package com.baber.saloonservice.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "t_saloon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Saloon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String adderss;
    private double rating;
    private int noOfReviews;
    private String imageUrl;
    private boolean userLiked;
    private String phoneNumber;
    private String description;
    private String openingDays;
    private String openTime;
    private String closeTime;
    private String location;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OfferItems> offers;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SaloonSpecialist> saloonSpecilists;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SaloonServics> saloonServics;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SaloonGallery> saloonGalleries;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SaloonReview> saloonReviews;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "saloon_category",
            joinColumns = @JoinColumn(name = "saloon_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<SaloonCategory> categories;
}
