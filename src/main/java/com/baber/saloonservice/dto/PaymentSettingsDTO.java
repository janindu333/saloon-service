package com.baber.saloonservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(description = "Payment settings configuration")
public class PaymentSettingsDTO {
    
    @Valid
    @NotNull(message = "Accepted payment methods are required")
    @JsonProperty("acceptedMethods")
    private AcceptedMethodsDTO acceptedMethods;
    
    @Valid
    @NotNull(message = "Deposit configuration is required")
    @JsonProperty("deposit")
    private DepositDTO deposit;
    
    @Valid
    @NotNull(message = "Cancellation policy is required")
    @JsonProperty("cancellation")
    private CancellationDTO cancellation;
    
    @Valid
    @NotNull(message = "No-show penalty is required")
    @JsonProperty("noShow")
    private NoShowDTO noShow;
    
    // Inner classes
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Accepted payment methods")
    public static class AcceptedMethodsDTO {
        @NotNull(message = "Cash payment method is required")
        @Schema(description = "Accept cash payments", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean cash;
        
        @NotNull(message = "Card payment method is required")
        @Schema(description = "Accept card payments", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean card;
        
        @NotNull(message = "Online payment method is required")
        @Schema(description = "Accept online payments", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean online;
        
        @NotNull(message = "Mobile payment method is required")
        @Schema(description = "Accept mobile payments", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean mobile;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Deposit configuration")
    public static class DepositDTO {
        @NotNull(message = "Deposit required flag is required")
        @Schema(description = "Whether deposit is required", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean required;
        
        @NotBlank(message = "Deposit type is required")
        @Schema(description = "Deposit type", example = "percentage", 
                allowableValues = {"fixed", "percentage"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String type;
        
        @NotNull(message = "Deposit amount is required")
        @Schema(description = "Deposit amount (fixed or percentage)", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Cancellation fee configuration")
    public static class CancellationFeeDTO {
        @NotBlank(message = "Cancellation fee type is required")
        @Schema(description = "Cancellation fee type", example = "none",
                allowableValues = {"none", "deposit", "percentage", "full"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String type;
        
        @Schema(description = "Percentage amount if type is percentage", example = "null", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private Double amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Cancellation policy")
    public static class CancellationDTO {
        @Schema(description = "Hours before appointment when cancellation is allowed (null = anytime)", example = "24", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer allowedUntil;
        
        @Valid
        @NotNull(message = "Cancellation fee is required")
        @JsonProperty("fee")
        private CancellationFeeDTO fee;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "No-show penalty configuration")
    public static class NoShowDTO {
        @NotBlank(message = "No-show penalty is required")
        @Schema(description = "No-show penalty type", example = "none",
                allowableValues = {"none", "full", "percentage", "block"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String penalty;
        
        @Schema(description = "Percentage amount if penalty is percentage", example = "null", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private Double amount;
    }
}
