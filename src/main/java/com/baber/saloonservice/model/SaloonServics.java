package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "t_saloon_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonServics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private int numberOfServiceTypes;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SaloonServiceType> saloonServiceType;

}
