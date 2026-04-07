package com.baber.saloonservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaloonReviewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String reviewText;
    private double rating;
    private String userName;
    private LocalDateTime createdAt;
} 