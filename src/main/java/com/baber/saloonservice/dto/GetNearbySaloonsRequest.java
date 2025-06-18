package com.baber.saloonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetNearbySaloonsRequest {
    private String userLatitude;
    private String userLongitude;

    private String distanceLimit;
}
