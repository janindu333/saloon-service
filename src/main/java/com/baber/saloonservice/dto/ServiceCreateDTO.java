package com.baber.saloonservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating a salon service")
public class ServiceCreateDTO {
    
    @NotBlank(message = "Service name is required")
    @Schema(description = "Service name", example = "Haircut", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @NotBlank(message = "Category is required")
    @Schema(description = "Service category", example = "haircut", 
            allowableValues = {"haircut", "coloring", "styling", "nails", "massage", "facial", "waxing", "other"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;
    
    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    @Schema(description = "Service duration in minutes", example = "30", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer duration;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    @Schema(description = "Service price", example = "25.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;
    
    @Schema(description = "Service description", example = "Professional haircut service", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;
}
