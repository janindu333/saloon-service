package com.baber.saloonservice.controller;

import com.baber.saloonservice.configurations.UserContext;
import com.baber.saloonservice.dto.*;
import com.baber.saloonservice.model.OfferItems;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.service.SaloonService;
import jakarta.validation.Valid;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/saloon")
public class SaloonController {
    private final SaloonService saloonService;
    private final RedisTemplate<String, Object> redisTemplate;

    public SaloonController(SaloonService saloonService, RedisTemplate<String, Object> redisTemplate) {
        this.saloonService = saloonService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<SaloonCreateResponse>> createSaloon(@Valid @RequestBody SaloonCreateDTO saloonCreateDTO) {
        try {
            // Check authentication - UserContextFilter should have populated this
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization - only owner and super_admin can create salons
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can create salons. Current role: " + role, null));
            }
            
            Saloon saloon = saloonService.createSaloon(saloonCreateDTO); // This automatically evicts the cache
            
            SaloonCreateResponse response = new SaloonCreateResponse();
            response.setSalonId(saloon.getPublicId().toString());
            response.setMessage("Salon created successfully");
            
            return ResponseEntity.ok(new BaseResponse<>(true, "Salon created successfully", 0, null, response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, 1, "Failed to create salon: " + e.getMessage(), null));
        }
    }

    @GetMapping("/getAll")
    public BaseResponse<List<SaloonDTO>> getSaloons() {
        try {
            // Directly call the service, let @Cacheable handle caching
            List<SaloonDTO> saloons = saloonService.getAllSaloons();
            return new BaseResponse<>(true, "success", 0, "", saloons);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure: " + e.getMessage(), 1, "", null);
        }
    }

    @GetMapping("/getById/{id}")
    public BaseResponse<SaloonDTO> getSaloonById(@PathVariable Long id) {
        try {
            SaloonDTO saloon = saloonService.getSaloonById(id);
            if (saloon != null) {
                return new BaseResponse<>(true, "Success", 0, "", saloon);
            } else {
                return new BaseResponse<>(false, "Saloon not found", 1, "", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure: " + e.getMessage(), 1, "", null);
        }
    }

    @PutMapping("/update/{id}")
    public BaseResponse<String> updateSaloon(@PathVariable Long id, @RequestBody Saloon saloon) {
        try {
            saloonService.updateSaloon(id, saloon);
            return new BaseResponse<>(true, "Saloon updated successfully", 0, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failed to update saloon: " + e.getMessage(), 1, "", null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deleteSaloon(@PathVariable Long id) {
        try {
            saloonService.deleteSaloon(id);
            return new BaseResponse<>(true, "Saloon deleted successfully", 0, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failed to delete saloon: " + e.getMessage(), 1, "", null);
        }
    }

    @GetMapping("/getByCategory/{categoryId}")
    public BaseResponse<List<SaloonDTO>> getSaloonsByCategory(@PathVariable Long categoryId) {
        try {
            List<SaloonDTO> saloons = saloonService.getSaloonsByCategory(categoryId);
            return new BaseResponse<>(true, "Success", 0, "", saloons);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failed to fetch saloons by category: " + e.getMessage(), 1, "", null);
        }
    }

    @GetMapping("/search")
    public BaseResponse<List<SaloonDTO>> searchSaloons(@RequestParam String query) {
        try {
            List<SaloonDTO> saloons = saloonService.searchSaloons(query);
            return new BaseResponse<>(true, "Success", 0, "", saloons);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failed to search saloons: " + e.getMessage(), 1, "", null);
        }
    }

    @PostMapping("/addReview")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<String> addReview(@RequestBody Saloon saloon) {

        try {
            saloonService.createSaloon(saloon);
            return new BaseResponse<>(true, "Success", 0, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure: " + e.getMessage(), 1, "", null);
        }
    }

    // New location management endpoints
    @PostMapping("/{saloonId}/addPrimaryLocation/{locationId}")
    public BaseResponse<String> addPrimaryLocationToSaloon(@PathVariable Long saloonId, @PathVariable Long locationId) {
        try {
            saloonService.addPrimaryLocationToSaloon(saloonId, locationId);
            return new BaseResponse<>(true, "Primary location added successfully", 0, "", null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to add primary location: " + e.getMessage(), 1, "", null);
        }
    }

    @PostMapping("/{saloonId}/addLocation/{locationId}")
    public BaseResponse<String> addLocationToSaloon(@PathVariable Long saloonId, @PathVariable Long locationId) {
        try {
            saloonService.addLocationToSaloon(saloonId, locationId);
            return new BaseResponse<>(true, "Location added successfully", 0, "", null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to add location: " + e.getMessage(), 1, "", null);
        }
    }

    @DeleteMapping("/{saloonId}/removeLocation/{locationId}")
    public BaseResponse<String> removeLocationFromSaloon(@PathVariable Long saloonId, @PathVariable Long locationId) {
        try {
            saloonService.removeLocationFromSaloon(saloonId, locationId);
            return new BaseResponse<>(true, "Location removed successfully", 0, "", null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to remove location: " + e.getMessage(), 1, "", null);
        }
    }

    @GetMapping("/getByLocation/{locationId}")
    public BaseResponse<List<Saloon>> getSaloonsByLocation(@PathVariable Long locationId) {
        try {
            List<Saloon> saloons = saloonService.getSaloonsByLocation(locationId);
            return new BaseResponse<>(true, "Success", 0, "", saloons);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch saloons: " + e.getMessage(), 1, "", null);
        }
    }

    @GetMapping("/getNearBySaloons")
    public BaseResponse<List<Saloon>> getNearBySaloons(
            @RequestParam String userLatitude,
            @RequestParam String userLongitude,
            @RequestParam String distanceLimit
    ) {
        // Use the parameters (userLatitude, userLongitude, distanceLimit) in your logic
        // Call your service to fetch saloons based on the provided parameters

        return new BaseResponse<>(true, "Success", 0, "",
                saloonService.getNearBySaloons(userLatitude, userLongitude, distanceLimit));
    }

    @GetMapping("/getOffers")
    public BaseResponse<List<OfferItems>> getOffers() {

        return new BaseResponse<>(true, "Success", 0, "",
                saloonService.getAllOfferItemsFromSaloons());
    }

    @PostMapping("/addOffers")
    public BaseResponse<String> addOffers(@RequestBody OfferRequest offerRequest) {
        try {
            saloonService.createOfferForSaloon(offerRequest);
            return new BaseResponse<>(true, "Success", 0, "",
                    null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "false", 0, "offer adding failed",
                    null);
        }
    }

    // ========== ONBOARDING ENDPOINTS ==========

    @PostMapping("/{salonId}/business-hours")
    public ResponseEntity<BaseResponse<String>> setBusinessHours(
            @PathVariable UUID salonId,
            @Valid @RequestBody BusinessHoursDTO businessHours) {
        try {
            // Check authentication
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can set business hours.", null));
            }
            
            saloonService.setBusinessHours(salonId, businessHours);
            return ResponseEntity.ok(new BaseResponse<>(true, "Business hours updated successfully", 0, null, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to update business hours: " + e.getMessage(), null));
        }
    }

    @PostMapping("/{salonId}/services")
    public ResponseEntity<BaseResponse<ServiceCreateResponse>> createService(
            @PathVariable UUID salonId,
            @Valid @RequestBody ServiceCreateDTO request) {
        try {
            // Check authentication
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can create services.", null));
            }
            
            Long serviceId = saloonService.createService(salonId, request);
            ServiceCreateResponse response = new ServiceCreateResponse();
            response.setServiceId(serviceId.toString());
            response.setMessage("Service created successfully");
            return ResponseEntity.ok(new BaseResponse<>(true, "Service created successfully", 0, null, response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to create service: " + e.getMessage(), null));
        }
    }
    
    @PatchMapping("/{salonId}/services/{serviceId}")
    public ResponseEntity<BaseResponse<String>> updateService(
            @PathVariable UUID salonId,
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceCreateDTO request) {
        try {
            // Check authentication
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can update services.", null));
            }
            
            saloonService.updateService(salonId, serviceId, request);
            return ResponseEntity.ok(new BaseResponse<>(true, "Service updated successfully", 0, null, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to update service: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{salonId}/services/{serviceId}")
    public ResponseEntity<BaseResponse<String>> deleteService(
            @PathVariable UUID salonId,
            @PathVariable Long serviceId) {
        try {
            // Check authentication
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can delete services.", null));
            }
            
            saloonService.deleteService(salonId, serviceId);
            return ResponseEntity.ok(new BaseResponse<>(true, "Service deleted successfully", 0, null, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to delete service: " + e.getMessage(), null));
        }
    }

    @PostMapping("/{salonId}/staff/invite")
    public ResponseEntity<BaseResponse<StaffInviteResponse>> inviteStaff(
            @PathVariable UUID salonId,
            @Valid @RequestBody StaffInviteDTO request) {
        try {
            // Check authentication
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can invite staff.", null));
            }
            
            String invitationId = saloonService.inviteStaff(salonId, request);
            StaffInviteResponse response = new StaffInviteResponse();
            response.setInvitationId(invitationId);
            response.setMessage("Invitation sent successfully");
            return ResponseEntity.ok(new BaseResponse<>(true, "Invitation sent successfully", 0, null, response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to send invitation: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{salonId}/staff/invitations")
    public ResponseEntity<BaseResponse<List<StaffInviteResponse>>> getInvitations(
            @PathVariable("salonId") UUID salonId) {
        try {
            // Check authentication
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can view invitations.", null));
            }
            
            List<com.baber.saloonservice.model.StaffInvitation> invitations = saloonService.getInvitations(salonId);
            List<StaffInviteResponse> responseList = invitations.stream()
                .map(inv -> {
                    StaffInviteResponse resp = new StaffInviteResponse();
                    resp.setInvitationId(inv.getInvitationToken().toString());
                    resp.setEmail(inv.getEmail());
                    resp.setFirstName(inv.getFirstName());
                    resp.setLastName(inv.getLastName());
                    resp.setRole(inv.getRole());
                    resp.setStatus(inv.getStatus());
                    resp.setMessage("Invitation retrieved");
                    return resp;
                })
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(new BaseResponse<>(true, "Invitations retrieved successfully", 0, null, responseList));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to retrieve invitations: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/staff/invitations/{invitationId}/resend")
    public ResponseEntity<BaseResponse<String>> resendInvitation(
            @PathVariable Long invitationId) {
        try {
            // Check authentication
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can resend invitations.", null));
            }
            
            saloonService.resendInvitationByInvitationId(invitationId);
            return ResponseEntity.ok(new BaseResponse<>(true, "Invitation resent successfully", 0, null, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to resend invitation: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/staff/invitations/{invitationId}")
    public ResponseEntity<BaseResponse<String>> cancelInvitation(
            @PathVariable Long invitationId) {
        try {
            // Check authentication
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can cancel invitations.", null));
            }
            
            saloonService.cancelInvitationByInvitationId(invitationId);
            return ResponseEntity.ok(new BaseResponse<>(true, "Invitation cancelled successfully", 0, null, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to cancel invitation: " + e.getMessage(), null));
        }
    }

    @PostMapping("/{salonId}/payment-settings")
    public ResponseEntity<BaseResponse<String>> setPaymentSettings(
            @PathVariable UUID salonId,
            @Valid @RequestBody PaymentSettingsRequestDTO request) {
        try {
            // Check authentication
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can set payment settings.", null));
            }
            
            saloonService.setPaymentSettings(salonId, request.getPaymentSettings());
            return ResponseEntity.ok(new BaseResponse<>(true, "Payment settings updated successfully", 0, null, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to update payment settings: " + e.getMessage(), null));
        }
    }

    @PatchMapping("/{salonId}/activate")
    public ResponseEntity<BaseResponse<String>> activateSalon(@PathVariable UUID salonId) {
        try {
            // Check authentication
            String role = UserContext.getRole();
            Long userId = UserContext.getUserId();
            
            if (role == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, null, HttpStatus.UNAUTHORIZED.value(), 
                        "Authentication required. Please provide a valid bearer token.", null));
            }
            
            // Check role-based authorization
            if (!UserContext.hasAnyRole("owner", "super_admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, null, HttpStatus.FORBIDDEN.value(), 
                        "Access denied. Only salon owners and super administrators can activate salons.", null));
            }
            
            saloonService.activateSalon(salonId);
            return ResponseEntity.ok(new BaseResponse<>(true, "Salon activated successfully", 0, null, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(false, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to activate salon: " + e.getMessage(), null));
        }
    }
}