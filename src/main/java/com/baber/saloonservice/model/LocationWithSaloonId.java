package com.baber.saloonservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_current_location_with_user_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationWithSaloonId extends Base{

    private Long saloonId;
    private String latitude;
    private String longitude;


}
