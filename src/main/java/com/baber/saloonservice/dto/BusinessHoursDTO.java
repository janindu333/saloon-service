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
@Schema(description = "Business hours for all days of the week")
public class BusinessHoursDTO {
    @Valid
    @NotNull(message = "Monday hours are required")
    @JsonProperty("monday")
    private DayHoursDTO monday;
    
    @Valid
    @NotNull(message = "Tuesday hours are required")
    @JsonProperty("tuesday")
    private DayHoursDTO tuesday;
    
    @Valid
    @NotNull(message = "Wednesday hours are required")
    @JsonProperty("wednesday")
    private DayHoursDTO wednesday;
    
    @Valid
    @NotNull(message = "Thursday hours are required")
    @JsonProperty("thursday")
    private DayHoursDTO thursday;
    
    @Valid
    @NotNull(message = "Friday hours are required")
    @JsonProperty("friday")
    private DayHoursDTO friday;
    
    @Valid
    @NotNull(message = "Saturday hours are required")
    @JsonProperty("saturday")
    private DayHoursDTO saturday;
    
    @Valid
    @NotNull(message = "Sunday hours are required")
    @JsonProperty("sunday")
    private DayHoursDTO sunday;
}
