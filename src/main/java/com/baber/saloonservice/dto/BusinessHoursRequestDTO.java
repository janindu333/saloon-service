package com.baber.saloonservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for setting business hours")
public class BusinessHoursRequestDTO {
    @Valid
    @NotNull(message = "Business hours are required")
    @JsonProperty("businessHours")
    @Schema(description = "Business hours for all days of the week", requiredMode = Schema.RequiredMode.REQUIRED)
    private BusinessHoursDTO businessHours;
}
