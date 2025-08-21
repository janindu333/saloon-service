package com.baber.saloonservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_saloon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DELETED = 0")
public class Saloon extends Base {
    @Transient
    private static final long serialVersionUID = -1L;
    private String name;
    private String address;
    private double rating;
    private int noOfReviews;
    private String imageUrl;
    private boolean userLiked;
    private String phoneNumber;
    private String description;
    private String openingDays;
    private String openTime;
    private String closeTime;
    // Primary location for the saloon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_location_id")
    private Location primaryLocation;
    
    // Multiple locations where this saloon operates (branch offices)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "saloon_locations", 
               joinColumns = @JoinColumn(name = "saloon_id"),
               inverseJoinColumns = @JoinColumn(name = "location_id"))
    private List<Location> locations = new ArrayList<>();

    public Saloon(String value) {
    }
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "saloon")
    @JsonManagedReference
    private List<OfferItems> offers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "saloon")
    @JsonManagedReference
    private List<SaloonServices> saloonServics;

    @OneToMany(cascade = CascadeType.ALL,mappedBy="saloon")
    @JsonManagedReference
    private List<SaloonReview> saloonReviews;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SaloonGallery> saloonGalleries;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "saloon_category", joinColumns = @JoinColumn(name = "saloon_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<SaloonCategory> categories;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "saloon_specialist", joinColumns = @JoinColumn(name = "saloon_id"),
            inverseJoinColumns = @JoinColumn(name = "specialist_id"))
    @JsonIgnore
    private List<SaloonSpecialist> specialists = new ArrayList<>();

    public List<OfferItems> getAllOfferItems() {
        return offers;
    }
}
