package com.baber.saloonservice.dto;

import com.baber.saloonservice.model.SaloonServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaloonServiceTypeRequest {
    private Long serviceId;
    private String name;
    private String duration;
    private String description;
    private String cost;



}
