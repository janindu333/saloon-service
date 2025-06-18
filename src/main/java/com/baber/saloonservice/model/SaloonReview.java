package com.baber.saloonservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_saloon_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonReview extends Base{

    @ManyToOne
    @JoinColumn(name="saloon_id", nullable=false)
    @JsonBackReference
    private Saloon saloon;
    private String customerImageUrl;
    private String reviewDesc;
    private LocalDateTime reviewAddedTime;
    private String rating;
}
