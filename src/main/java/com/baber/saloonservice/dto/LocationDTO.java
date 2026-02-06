package com.baber.saloonservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Location information for salon")
public class LocationDTO {
    
    @Schema(description = "Location ID (optional for new locations)", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;
    
    @NotBlank(message = "Location name is required")
    @Schema(description = "Location name", example = "Main Branch", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @NotBlank(message = "Address is required")
    @Schema(description = "Location address", example = "123 Main Street", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;
    
    @Schema(description = "City", example = "Colombo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String city;
    
    @Schema(description = "State/Province", example = "Western Province", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String state;
    
    @Schema(description = "Country", example = "Sri Lanka", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String country;
    
    @Schema(description = "Postal code", example = "00100", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String postalCode;
    
    @NotBlank(message = "Latitude is required")
    @Schema(description = "Latitude coordinate", example = "6.9271", requiredMode = Schema.RequiredMode.REQUIRED)
    private String latitude;
    
    @NotBlank(message = "Longitude is required")
    @Schema(description = "Longitude coordinate", example = "79.8612", requiredMode = Schema.RequiredMode.REQUIRED)
    private String longitude;
    
    @Schema(description = "Location description", example = "Main salon location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;
    
    @Schema(description = "Timezone", example = "Asia/Colombo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String timezone;
    
    @Schema(description = "Formatted address", example = "123 Main St, Colombo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String formattedAddress;
    
    @Schema(description = "Google Place ID", example = "ChIJ...", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String placeId;
} 