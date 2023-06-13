package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_saloon_services_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String duraton;
    private String description;
    private String cost;
}
