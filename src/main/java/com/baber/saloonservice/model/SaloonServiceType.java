package com.baber.saloonservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "t_saloon_services_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DELETED = 0")
@SQLDelete(sql = "UPDATE t_saloon_services_types SET deleted = 1 WHERE id = ? and version = ?")
public class SaloonServiceType extends Base {
    private String name;
    private String duration;
    private String description;
    private String cost;

    private String imageUrl;
    @ManyToOne // Many service types can belong to one saloon service
    @JoinColumn(name = "saloon_service_id") // Specifies the foreign key column name
    @JsonBackReference
    private SaloonServices saloonService;
}
