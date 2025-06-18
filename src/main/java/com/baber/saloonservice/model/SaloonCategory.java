package com.baber.saloonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; 

@Entity
@Table(name = "t_saloon_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaloonCategory extends Base{

    private String name;


}
