package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_offer_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String saloonName;
    private double amountOfOffer;
}
