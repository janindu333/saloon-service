package com.baber.saloonservice.dto;

import com.baber.saloonservice.model.SaloonServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaloonServiceRequest {

    private Long saloonId;
    private String name;

    private List<SaloonServiceType> saloonServiceType;

}
