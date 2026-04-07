package com.baber.saloonservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating a new salon (Quick Setup)")
public class SaloonCreateDTO {
    
    @NotBlank(message = "Salon name is required")
    @Schema(description = "Salon name", example = "Hair Studio", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @NotBlank(message = "Address is required")
    @Schema(description = "Full salon address", example = "123 Main Street, Colombo, Western Province, Sri Lanka", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;
    
    @NotBlank(message = "Phone number is required")
    @Schema(description = "Contact phone number", example = "0712345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
    
    @Schema(description = "Website URL", example = "https://hairstudio.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String website;
    
    @NotNull(message = "Latitude is required")
    @Schema(description = "Latitude coordinate", example = "6.9271", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    @Schema(description = "Longitude coordinate", example = "79.8612", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;
} 