package com.baber.saloonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaloonReviewRequest {

    private String reviewDescription;
    private String rating;
    private Long userId;
    private Long saloonId;

}
