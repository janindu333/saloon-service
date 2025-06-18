package com.baber.saloonservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "t_saloon_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonServices extends Base{
    private String name;
    @ManyToOne // Many services can belong to one saloon
    @JoinColumn(name = "saloon_id") // Specifies the foreign key column name
    @JsonBackReference
    private Saloon saloon;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "saloonService") // Add mappedBy attribute
    @JsonManagedReference
    private List<SaloonServiceType> saloonServiceType;
    public void addSaloonServiceType(SaloonServiceType serviceType) {
        if (saloonServiceType == null) {
            saloonServiceType = new ArrayList<>();
        }
        serviceType.setSaloonService(this);
        saloonServiceType.add(serviceType);
    }
}
