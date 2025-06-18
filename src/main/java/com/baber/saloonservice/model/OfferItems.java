package com.baber.saloonservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class OfferItems extends Base{
    private String saloonName;
    private double amountOfOffer;
    @ManyToOne // Many offers can belong to one saloon
    @JoinColumn(name = "saloon_id") // Specifies the foreign key column name
    @JsonBackReference
    private Saloon saloon;
}
