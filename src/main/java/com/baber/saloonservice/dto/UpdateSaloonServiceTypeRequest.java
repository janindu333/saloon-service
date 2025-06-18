package com.baber.saloonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSaloonServiceTypeRequest {

    private Long id;
    private String name;
    private String duration;
    private String description;
    private String cost;

}
