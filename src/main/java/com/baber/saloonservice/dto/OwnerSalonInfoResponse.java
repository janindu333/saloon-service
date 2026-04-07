package com.baber.saloonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lightweight summary of an owner's salon used for cross-service
 * communication (e.g., from identity-service during login).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerSalonInfoResponse {

    /**
     * Public UUID of the salon as a string.
     */
    private String saloonId;

    /**
     * Optional high-level status of the salon.
     * Currently not populated by saloon-service; the identity-service
     * derives a value such as "pending_setup" or "active" based on
     * onboarding status.
     */
    private String salonStatus;

    /**
     * Whether the salon has at least one business hours entry set.
     * Used by identity-service to auto-complete onboarding step "business_hours".
     */
    private Boolean hasBusinessHours;

    /**
     * Whether the salon has at least one service defined.
     * Used by identity-service to auto-complete onboarding step "services_setup".
     */
    private Boolean hasServices;

    /**
     * Whether at least one staff invitation has been sent for this salon.
     * Used by identity-service to auto-complete onboarding step "staff_invitation".
     */
    private Boolean hasStaffInvite;
}

