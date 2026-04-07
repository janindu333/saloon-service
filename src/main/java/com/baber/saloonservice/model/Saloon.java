package com.baber.saloonservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID publicId;
    
    @Column(nullable = false)
    private Long ownerId; // User ID from identity-service
    
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
    // Cascade PERSIST so that a newly created Location is saved automatically
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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
