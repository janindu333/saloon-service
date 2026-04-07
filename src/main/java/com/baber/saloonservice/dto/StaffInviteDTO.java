package com.baber.saloonservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for inviting staff member")
public class StaffInviteDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Staff member's email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    
    @NotBlank(message = "First name is required")
    @Schema(description = "Staff member's first name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Schema(description = "Staff member's last name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;
    
    @NotBlank(message = "Role is required")
    @Schema(description = "Staff role", example = "staff", 
            allowableValues = {"staff", "manager", "receptionist"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;
}
