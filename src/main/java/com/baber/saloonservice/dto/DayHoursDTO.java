package com.baber.saloonservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Business hours for a single day")
public class DayHoursDTO {
    @NotNull(message = "isOpen is required")
    @Schema(description = "Whether salon is open on this day", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isOpen;
    
    @Schema(description = "Opening time in HH:MM format (24-hour). Required when isOpen is true.", example = "09:00")
    private String open;
    
    @Schema(description = "Closing time in HH:MM format (24-hour). Required when isOpen is true.", example = "18:00")
    private String close;
}
