package com.baber.saloonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferItemsDto {

    private Long id;
    private String saloonName;
    private double amountOfOffer;
}
