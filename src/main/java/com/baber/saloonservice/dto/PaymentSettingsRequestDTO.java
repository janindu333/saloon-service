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
@Schema(description = "Request DTO for payment settings")
public class PaymentSettingsRequestDTO {
    @Valid
    @NotNull(message = "Payment settings are required")
    @JsonProperty("paymentSettings")
    @Schema(description = "Payment settings configuration", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentSettingsDTO paymentSettings;
}
