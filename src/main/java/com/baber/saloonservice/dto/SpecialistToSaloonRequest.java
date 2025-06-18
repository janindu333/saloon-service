package com.baber.saloonservice.dto;

import com.baber.saloonservice.model.SaloonSpecialist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistToSaloonRequest {
    private Long saloonId;
    private Long specialistId;
}
