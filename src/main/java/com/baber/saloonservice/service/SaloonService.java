package com.baber.saloonservice.service;

import com.baber.saloonservice.dto.*;
import com.baber.saloonservice.model.*;
import com.baber.saloonservice.model.Location;
import com.baber.saloonservice.repository.SaloonRepository;
import com.baber.saloonservice.repository.BusinessHoursRepository;
import com.baber.saloonservice.repository.StaffInvitationRepository;
import com.baber.saloonservice.repository.PaymentSettingsRepository;
import com.baber.saloonservice.repository.SaloonServiceRepository;
import com.baber.saloonservice.configurations.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SaloonService {

    private final SaloonRepository saloonRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final StaffInvitationRepository staffInvitationRepository;
    private final PaymentSettingsRepository paymentSettingsRepository;
    private final SaloonServiceRepository saloonServiceRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final double EARTH_RADIUS = 6371; // Earth's radius in kilometers

    @Autowired
    public SaloonService(SaloonRepository saloonRepository, 
                         BusinessHoursRepository businessHoursRepository,
                         StaffInvitationRepository staffInvitationRepository,
                         PaymentSettingsRepository paymentSettingsRepository,
                         SaloonServiceRepository saloonServiceRepository,
                         RedisTemplate<String, Object> redisTemplate) {
        this.saloonRepository = saloonRepository;
        this.businessHoursRepository = businessHoursRepository;
        this.staffInvitationRepository = staffInvitationRepository;
        this.paymentSettingsRepository = paymentSettingsRepository;
        this.saloonServiceRepository = saloonServiceRepository;
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * Validate that the salon belongs to the authenticated user
     */
    private void validateSalonOwnership(UUID salonId) {
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        Long currentUserId = UserContext.getUserId();
        
        if (currentUserId == null) {
            throw new IllegalArgumentException("User not authenticated");
        }
        
        if (saloon.getOwnerId() == null || !saloon.getOwnerId().equals(currentUserId)) {
            throw new IllegalArgumentException("Access denied. You do not own this salon.");
        }
    }


//    public void createSaloon(Saloon saloon) {
//        // Clear the cache manually
//        redisTemplate.delete("saloonsCache");
//        saloonRepository.save(saloon);
//    }

    @CacheEvict(value = "saloonsCache", allEntries = true)
    public void createSaloon(Saloon saloon) {
        saloonRepository.save(saloon);
    }

    @CacheEvict(value = "saloonsCache", allEntries = true)
    public Saloon createSaloon(SaloonCreateDTO saloonCreateDTO) {
        Saloon saloon = convertToSaloon(saloonCreateDTO);
        // Generate UUID for publicId
        saloon.setPublicId(UUID.randomUUID());
        // Set ownerId from UserContext (set by UserContextFilter)
        Long ownerId = com.baber.saloonservice.configurations.UserContext.getUserId();
        if (ownerId != null) {
            saloon.setOwnerId(ownerId);
        }
        return saloonRepository.save(saloon);
    }

    private Saloon convertToSaloon(SaloonCreateDTO dto) {
        Saloon saloon = new Saloon();
        saloon.setName(dto.getName().trim());
        saloon.setAddress(dto.getAddress().trim());
        saloon.setPhoneNumber(dto.getPhone().trim());
        
        // Set website in description if provided (can be moved to dedicated field later)
        if (dto.getWebsite() != null && !dto.getWebsite().trim().isEmpty()) {
            saloon.setDescription("Website: " + dto.getWebsite().trim());
        }
        
        // Set default values for computed/user-specific fields
        saloon.setRating(0.0);
        saloon.setNoOfReviews(0);
        saloon.setUserLiked(false);
        
        // Create and set primary location from latitude/longitude
        Location location = new Location();
        location.setName(dto.getName() + " - Main Branch");
        location.setAddress(dto.getAddress());
        location.setLatitude(String.valueOf(dto.getLatitude()));
        location.setLongitude(String.valueOf(dto.getLongitude()));
        location.setFormattedAddress(dto.getAddress());
        saloon.setPrimaryLocation(location);
        
        return saloon;
    }

    private Location convertToLocation(LocationDTO locationDTO) {
        Location location = new Location();
        location.setName(locationDTO.getName());
        location.setAddress(locationDTO.getAddress());
        location.setCity(locationDTO.getCity());
        location.setState(locationDTO.getState());
        location.setCountry(locationDTO.getCountry());
        location.setPostalCode(locationDTO.getPostalCode());
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());
        location.setDescription(locationDTO.getDescription());
        location.setTimezone(locationDTO.getTimezone());
        location.setFormattedAddress(locationDTO.getFormattedAddress());
        location.setPlaceId(locationDTO.getPlaceId());
        return location;
    }

    @Cacheable(value = "saloonsCache")
    public List<SaloonDTO> getAllSaloons() {
        List<Saloon> saloons = saloonRepository.findAll();
        System.out.println("Fetching saloons from database, not cache");
        return saloons.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private SaloonDTO convertToDTO(Saloon saloon) {
        SaloonDTO dto = new SaloonDTO();
        dto.setId(saloon.getId());
        dto.setPublicId(saloon.getPublicId());
        dto.setName(saloon.getName());
        dto.setAddress(saloon.getAddress());
        dto.setRating(saloon.getRating());
        dto.setNoOfReviews(saloon.getNoOfReviews());
        dto.setImageUrl(saloon.getImageUrl());
        dto.setUserLiked(saloon.isUserLiked());
        dto.setPhoneNumber(saloon.getPhoneNumber());
        dto.setDescription(saloon.getDescription());
        dto.setOpeningDays(saloon.getOpeningDays());
        dto.setOpenTime(saloon.getOpenTime());
        dto.setCloseTime(saloon.getCloseTime());
        
        // Convert primary location
        if (saloon.getPrimaryLocation() != null) {
            dto.setPrimaryLocation(convertToLocationDTO(saloon.getPrimaryLocation()));
        }
        
        // Convert locations
        if (saloon.getLocations() != null) {
            dto.setLocations(saloon.getLocations().stream()
                    .map(this::convertToLocationDTO)
                    .collect(Collectors.toList()));
        }
        
        // Convert categories
        if (saloon.getCategories() != null) {
            dto.setCategories(saloon.getCategories().stream()
                    .map(this::convertToCategoryDTO)
                    .collect(Collectors.toList()));
        }
        
        // Convert services
        if (saloon.getSaloonServics() != null) {
            dto.setServices(saloon.getSaloonServics().stream()
                    .map(this::convertToServicesDTO)
                    .collect(Collectors.toList()));
        }
        
        // Convert reviews
        if (saloon.getSaloonReviews() != null) {
            dto.setReviews(saloon.getSaloonReviews().stream()
                    .map(this::convertToReviewDTO)
                    .collect(Collectors.toList()));
        }
        
        // Convert specialists
        if (saloon.getSpecialists() != null) {
            dto.setSpecialists(saloon.getSpecialists().stream()
                    .map(this::convertToSpecialistDTO)
                    .collect(Collectors.toList()));
        }
        
        // Convert offers
        if (saloon.getOffers() != null) {
            dto.setOffers(saloon.getOffers().stream()
                    .map(this::convertToOfferDTO)
                    .collect(Collectors.toList()));
        }
        
        // Convert gallery
        if (saloon.getSaloonGalleries() != null) {
            dto.setGallery(saloon.getSaloonGalleries().stream()
                    .map(this::convertToGalleryDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private LocationDTO convertToLocationDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setAddress(location.getAddress());
        dto.setCity(location.getCity());
        dto.setState(location.getState());
        dto.setCountry(location.getCountry());
        dto.setPostalCode(location.getPostalCode());
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setDescription(location.getDescription());
        dto.setTimezone(location.getTimezone());
        dto.setFormattedAddress(location.getFormattedAddress());
        dto.setPlaceId(location.getPlaceId());
        return dto;
    }
    
    private SaloonCategoryDTO convertToCategoryDTO(SaloonCategory category) {
        SaloonCategoryDTO dto = new SaloonCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
    
    private SaloonServicesDTO convertToServicesDTO(SaloonServices service) {
        SaloonServicesDTO dto = new SaloonServicesDTO();
        dto.setId(service.getId());
        dto.setName(service.getName());
        // Add other fields as needed
        return dto;
    }
    
    private SaloonReviewDTO convertToReviewDTO(SaloonReview review) {
        SaloonReviewDTO dto = new SaloonReviewDTO();
        dto.setId(review.getId());
        dto.setReviewText(review.getReviewDesc());
        dto.setRating(Double.parseDouble(review.getRating()));
        dto.setUserName("User"); // Default value, you might want to get from user context
        dto.setCreatedAt(review.getReviewAddedTime());
        return dto;
    }
    
    private SaloonSpecialistDTO convertToSpecialistDTO(SaloonSpecialist specialist) {
        SaloonSpecialistDTO dto = new SaloonSpecialistDTO();
        dto.setId(specialist.getId());
        dto.setName(specialist.getName());
        dto.setPossition(specialist.getPossition());
        dto.setImage(specialist.getImage());
        dto.setNumber(specialist.getNumber());
        dto.setAbout(specialist.getAbout());
        
        // Set saloon IDs
        if (specialist.getSaloons() != null) {
            dto.setSaloonIds(specialist.getSaloons().stream()
                    .map(Saloon::getId)
                    .collect(Collectors.toSet()));
        }
        
        // Set hairstyle image IDs
        if (specialist.getHairstyleImages() != null) {
            dto.setHairstyleImageIds(specialist.getHairstyleImages().stream()
                    .map(SaloonSpecialistHairStyleImages::getId)
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }
    
    private OfferItemsDto convertToOfferDTO(OfferItems offer) {
        OfferItemsDto dto = new OfferItemsDto();
        dto.setId(offer.getId());
        dto.setSaloonName(offer.getSaloonName());
        dto.setAmountOfOffer(offer.getAmountOfOffer());
        return dto;
    }
    
    private SaloonGalleryDTO convertToGalleryDTO(SaloonGallery gallery) {
        SaloonGalleryDTO dto = new SaloonGalleryDTO();
        dto.setId(gallery.getId());
        dto.setImageUrl(gallery.getImageUrl());
        return dto;
    }

    public Optional<Saloon> findById(Long id) {
        return saloonRepository.findById(id);
    }
    
    public Optional<Saloon> findByPublicId(UUID publicId) {
        return saloonRepository.findByPublicId(publicId);
    }
    
    private Saloon findSalonByPublicIdOrThrow(UUID salonId) {
        return saloonRepository.findByPublicId(salonId)
                .orElseThrow(() -> new IllegalArgumentException("Salon not found"));
    }

    public Set<Long> findSaloonIdsBySpecialistId(Long id) {
        return saloonRepository.findSaloonIdsBySpecialistId(id);
    }

    public List<Saloon> getNearBySaloons(String userLatitudeReq, String userLongitudeReq, String distanceLimitReq) {
        double userLatitude = Double.parseDouble(userLatitudeReq);
        double userLongitude = Double.parseDouble(userLongitudeReq);
        double distanceLimit = Double.parseDouble(distanceLimitReq);

        List<Saloon> allSaloons = saloonRepository.findAll();
        List<Saloon> nearbySaloons = new ArrayList<>();

        for (Saloon saloon : allSaloons) {
            // Check primary location first
            if (saloon.getPrimaryLocation() != null) {
                double saloonLatitude = Double.parseDouble(saloon.getPrimaryLocation().getLatitude());
                double saloonLongitude = Double.parseDouble(saloon.getPrimaryLocation().getLongitude());
                double distance = calculateDistance(userLatitude, userLongitude, saloonLatitude, saloonLongitude);
                if (distance <= distanceLimit) {
                    nearbySaloons.add(saloon);
                    continue; // Skip checking other locations if primary location is within range
                }
            }
            
            // Check other locations
            if (saloon.getLocations() != null) {
                for (Location location : saloon.getLocations()) {
                    double saloonLatitude = Double.parseDouble(location.getLatitude());
                    double saloonLongitude = Double.parseDouble(location.getLongitude());
                    double distance = calculateDistance(userLatitude, userLongitude, saloonLatitude, saloonLongitude);
                    if (distance <= distanceLimit) {
                        nearbySaloons.add(saloon);
                        break; // Add saloon only once even if multiple locations are within range
                    }
                }
            }
        }
        return nearbySaloons;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    public List<OfferItems> getAllOfferItemsFromSaloons() {
        List<OfferItems> allOfferItems = new ArrayList<>();
        List<Saloon> saloons = saloonRepository.findAll();
        for (Saloon saloon : saloons) {
            allOfferItems.addAll(saloon.getAllOfferItems());
        }
        return allOfferItems;
    }

    public OfferItems createOfferForSaloon(OfferRequest offerRequest) {
        Saloon saloon = saloonRepository.findById(offerRequest.getSaloonId())
                .orElseThrow(() -> new IllegalArgumentException("Saloon not found"));

        OfferItems offer = new OfferItems();
        offer.setSaloonName(saloon.getName());
        offer.setAmountOfOffer(offerRequest.getAmountOfOffer());
        offer.setSaloon(saloon);

        List<OfferItems> offers = saloon.getOffers();
        offers.add(offer);
        saloon.setOffers(offers);
        saloonRepository.save(saloon);

        return offer;
    }
    
    // New methods for location management
    public Saloon addPrimaryLocationToSaloon(Long saloonId, Long locationId) {
        Saloon saloon = saloonRepository.findById(saloonId)
                .orElseThrow(() -> new IllegalArgumentException("Saloon not found"));
        
        // This would require LocationService injection
        // For now, we'll just return the saloon
        return saloon;
    }
    
    public Saloon addLocationToSaloon(Long saloonId, Long locationId) {
        Saloon saloon = saloonRepository.findById(saloonId)
                .orElseThrow(() -> new IllegalArgumentException("Saloon not found"));
        
        // This would require LocationService injection
        // For now, we'll just return the saloon
        return saloon;
    }
    
    public void removeLocationFromSaloon(Long saloonId, Long locationId) {
        Saloon saloon = saloonRepository.findById(saloonId)
                .orElseThrow(() -> new IllegalArgumentException("Saloon not found"));
        
        // Remove location from the many-to-many relationship
        if (saloon.getLocations() != null) {
            saloon.getLocations().removeIf(location -> location.getId().equals(locationId));
            saloonRepository.save(saloon);
        }
    }
    
    public List<Saloon> getSaloonsByLocation(Long locationId) {
        return saloonRepository.findByLocationId(locationId);
    }
    
    // New methods for the additional endpoints
    public SaloonDTO getSaloonById(Long id) {
        Optional<Saloon> saloon = saloonRepository.findById(id);
        return saloon.map(this::convertToDTO).orElse(null);
    }
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    public void updateSaloon(Long id, Saloon updatedSaloon) {
        Optional<Saloon> existingSaloon = saloonRepository.findById(id);
        if (existingSaloon.isPresent()) {
            Saloon saloon = existingSaloon.get();
            // Update fields
            saloon.setName(updatedSaloon.getName());
            saloon.setAddress(updatedSaloon.getAddress());
            saloon.setRating(updatedSaloon.getRating());
            saloon.setNoOfReviews(updatedSaloon.getNoOfReviews());
            saloon.setImageUrl(updatedSaloon.getImageUrl());
            saloon.setUserLiked(updatedSaloon.isUserLiked());
            saloon.setPhoneNumber(updatedSaloon.getPhoneNumber());
            saloon.setDescription(updatedSaloon.getDescription());
            saloon.setOpeningDays(updatedSaloon.getOpeningDays());
            saloon.setOpenTime(updatedSaloon.getOpenTime());
            saloon.setCloseTime(updatedSaloon.getCloseTime());
            saloon.setPrimaryLocation(updatedSaloon.getPrimaryLocation());
            saloon.setLocations(updatedSaloon.getLocations());
            saloon.setCategories(updatedSaloon.getCategories());
            saloon.setSpecialists(updatedSaloon.getSpecialists());
            
            saloonRepository.save(saloon);
        } else {
            throw new IllegalArgumentException("Saloon not found with id: " + id);
        }
    }
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    public void deleteSaloon(Long id) {
        if (saloonRepository.existsById(id)) {
            saloonRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Saloon not found with id: " + id);
        }
    }
    
    public List<SaloonDTO> getSaloonsByCategory(Long categoryId) {
        List<Saloon> saloons = saloonRepository.findByCategoryId(categoryId);
        return saloons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<SaloonDTO> searchSaloons(String query) {
        List<Saloon> saloons = saloonRepository.searchSaloons(query);
        return saloons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // ========== ONBOARDING SERVICE METHODS ==========
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    @Transactional
    public void setBusinessHours(UUID salonId, BusinessHoursDTO businessHours) {
        validateSalonOwnership(salonId);
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        
        // Validate time format and logic
        validateBusinessHours(businessHours);
        
        // Delete existing business hours for this salon
        businessHoursRepository.deleteBySalonId(saloon.getId());
        
        // Create new business hours entries
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        DayHoursDTO[] dayHours = {
            businessHours.getMonday(), businessHours.getTuesday(), businessHours.getWednesday(),
            businessHours.getThursday(), businessHours.getFriday(), businessHours.getSaturday(),
            businessHours.getSunday()
        };
        
        for (int i = 0; i < days.length; i++) {
            BusinessHours bh = new BusinessHours();
            bh.setSalon(saloon);
            bh.setDayOfWeek(days[i]);
            bh.setIsOpen(dayHours[i].getIsOpen());
            bh.setOpenTime(dayHours[i].getIsOpen() ? dayHours[i].getOpen() : null);
            bh.setCloseTime(dayHours[i].getIsOpen() ? dayHours[i].getClose() : null);
            businessHoursRepository.save(bh);
        }
    }
    
    private String formatDayHours(String day, DayHoursDTO dayHours) {
        if (dayHours.getIsOpen()) {
            return day + ":" + dayHours.getOpen() + "-" + dayHours.getClose();
        } else {
            return day + ":closed";
        }
    }
    
    private void validateBusinessHours(BusinessHoursDTO businessHours) {
        // Validate time format HH:MM and that close > open for open days
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        DayHoursDTO[] dayHours = {
            businessHours.getMonday(), businessHours.getTuesday(), businessHours.getWednesday(),
            businessHours.getThursday(), businessHours.getFriday(), businessHours.getSaturday(),
            businessHours.getSunday()
        };
        
        for (int i = 0; i < dayHours.length; i++) {
            DayHoursDTO day = dayHours[i];
            if (day.getIsOpen()) {
                if (day.getOpen() == null || day.getClose() == null) {
                    throw new IllegalArgumentException("Opening and closing times are required for " + days[i] + " when the salon is open.");
                }
                if (!isValidTimeFormat(day.getOpen()) || !isValidTimeFormat(day.getClose())) {
                    throw new IllegalArgumentException("Invalid time format for " + days[i] + ". Use HH:MM format (24-hour).");
                }
                if (compareTimes(day.getClose(), day.getOpen()) <= 0) {
                    throw new IllegalArgumentException("Closing time must be after opening time for " + days[i]);
                }
            }
        }
    }
    
    private boolean isValidTimeFormat(String time) {
        return time != null && time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }
    
    private int compareTimes(String time1, String time2) {
        String[] parts1 = time1.split(":");
        String[] parts2 = time2.split(":");
        int minutes1 = Integer.parseInt(parts1[0]) * 60 + Integer.parseInt(parts1[1]);
        int minutes2 = Integer.parseInt(parts2[0]) * 60 + Integer.parseInt(parts2[1]);
        return Integer.compare(minutes1, minutes2);
    }
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    public Long createService(UUID salonId, ServiceCreateDTO request) {
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        
        // Validate category
        String[] validCategories = {"haircut", "coloring", "styling", "nails", "massage", "facial", "waxing", "other"};
        boolean isValidCategory = false;
        for (String cat : validCategories) {
            if (cat.equalsIgnoreCase(request.getCategory())) {
                isValidCategory = true;
                break;
            }
        }
        if (!isValidCategory) {
            throw new IllegalArgumentException("Invalid category. Must be one of: haircut, coloring, styling, nails, massage, facial, waxing, other");
        }
        
        // Create service using existing SaloonServices model
        SaloonServices service = new SaloonServices();
        service.setName(request.getName().trim());
        service.setSaloon(saloon);
        
        // Create service type with duration, price, category, description
        SaloonServiceType serviceType = new SaloonServiceType();
        serviceType.setName(request.getName() + " - " + request.getCategory());
        serviceType.setDuration(String.valueOf(request.getDuration()));
        serviceType.setCost(String.valueOf(request.getPrice()));
        serviceType.setDescription(request.getDescription() != null ? request.getDescription() : "");
        serviceType.setSaloonService(service);
        
        // Add service type to service
        if (service.getSaloonServiceType() == null) {
            service.setSaloonServiceType(new ArrayList<>());
        }
        service.getSaloonServiceType().add(serviceType);
        
        // Add service to salon
        if (saloon.getSaloonServics() == null) {
            saloon.setSaloonServics(new ArrayList<>());
        }
        saloon.getSaloonServics().add(service);
        saloonRepository.save(saloon);
        
        return service.getId();
    }
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    @Transactional
    public void updateService(UUID salonId, Long serviceId, ServiceCreateDTO request) {
        validateSalonOwnership(salonId);
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        
        // Find the service
        Optional<SaloonServices> serviceOpt = saloon.getSaloonServics().stream()
            .filter(s -> s.getId().equals(serviceId))
            .findFirst();
        
        if (serviceOpt.isEmpty()) {
            throw new IllegalArgumentException("Service not found");
        }
        
        SaloonServices service = serviceOpt.get();
        
        // Validate category
        String[] validCategories = {"haircut", "coloring", "styling", "nails", "massage", "facial", "waxing", "other"};
        boolean isValidCategory = false;
        for (String cat : validCategories) {
            if (cat.equalsIgnoreCase(request.getCategory())) {
                isValidCategory = true;
                break;
            }
        }
        if (!isValidCategory) {
            throw new IllegalArgumentException("Invalid category. Must be one of: haircut, coloring, styling, nails, massage, facial, waxing, other");
        }
        
        // Update service name
        service.setName(request.getName().trim());
        
        // Update service type (assuming first one, or create new if none)
        if (service.getSaloonServiceType() == null || service.getSaloonServiceType().isEmpty()) {
            SaloonServiceType serviceType = new SaloonServiceType();
            serviceType.setName(request.getName() + " - " + request.getCategory());
            serviceType.setDuration(String.valueOf(request.getDuration()));
            serviceType.setCost(String.valueOf(request.getPrice()));
            serviceType.setDescription(request.getDescription() != null ? request.getDescription() : "");
            serviceType.setSaloonService(service);
            service.setSaloonServiceType(new ArrayList<>());
            service.getSaloonServiceType().add(serviceType);
        } else {
            // Update first service type
            SaloonServiceType serviceType = service.getSaloonServiceType().get(0);
            serviceType.setName(request.getName() + " - " + request.getCategory());
            serviceType.setDuration(String.valueOf(request.getDuration()));
            serviceType.setCost(String.valueOf(request.getPrice()));
            serviceType.setDescription(request.getDescription() != null ? request.getDescription() : "");
        }
        
        saloonRepository.save(saloon);
    }
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    @Transactional
    public void deleteService(UUID salonId, Long serviceId) {
        validateSalonOwnership(salonId);
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        
        // Find and remove the service
        boolean removed = saloon.getSaloonServics().removeIf(s -> s.getId().equals(serviceId));
        
        if (!removed) {
            throw new IllegalArgumentException("Service not found");
        }
        
        saloonRepository.save(saloon);
    }
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    @Transactional
    public String inviteStaff(UUID salonId, StaffInviteDTO request) {
        validateSalonOwnership(salonId);
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        
        // Validate role
        String[] validRoles = {"staff", "manager", "receptionist"};
        boolean isValidRole = false;
        for (String role : validRoles) {
            if (role.equalsIgnoreCase(request.getRole())) {
                isValidRole = true;
                break;
            }
        }
        if (!isValidRole) {
            throw new IllegalArgumentException("Invalid role. Must be one of: staff, manager, receptionist");
        }
        
        // Check if there's already a pending invitation for this email
        Optional<StaffInvitation> existingInvitation = staffInvitationRepository
            .findBySalonAndEmailAndStatus(saloon, request.getEmail(), "pending");
        
        if (existingInvitation.isPresent()) {
            throw new IllegalArgumentException("An invitation has already been sent to this email address");
        }
        
        // Create and save invitation
        StaffInvitation invitation = new StaffInvitation();
        invitation.setSalon(saloon);
        invitation.setEmail(request.getEmail());
        invitation.setFirstName(request.getFirstName());
        invitation.setLastName(request.getLastName());
        invitation.setRole(request.getRole().toLowerCase());
        invitation.setStatus("pending");
        
        staffInvitationRepository.save(invitation);
        
        // TODO: Integrate with email service to send invitation
        // emailService.sendInvitationEmail(request.getEmail(), invitation.getInvitationToken().toString(), salonId);
        
        return invitation.getInvitationToken().toString();
    }
    
    public List<StaffInvitation> getInvitations(UUID salonId) {
        validateSalonOwnership(salonId);
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        return staffInvitationRepository.findBySalonIdOrderByCreatedAtDesc(saloon.getId());
    }
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    @Transactional
    public void resendInvitation(UUID salonId, Long invitationId) {
        validateSalonOwnership(salonId);
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        
        StaffInvitation invitation = staffInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        
        if (!invitation.getSalon().getId().equals(saloon.getId())) {
            throw new IllegalArgumentException("Invitation does not belong to this salon");
        }
        
        if (!"pending".equals(invitation.getStatus())) {
            throw new IllegalArgumentException("Can only resend pending invitations");
        }
        
        // Update expiration
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));
        staffInvitationRepository.save(invitation);
        
        // TODO: Send email
        // emailService.sendInvitationEmail(invitation.getEmail(), invitation.getInvitationToken().toString(), salonId);
    }
    
    /**
     * Resend invitation by invitationId only (extracts salonId from invitation)
     */
    @CacheEvict(value = "saloonsCache", allEntries = true)
    @Transactional
    public void resendInvitationByInvitationId(Long invitationId) {
        StaffInvitation invitation = staffInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        
        // Validate ownership through the invitation's salon
        UUID salonId = invitation.getSalon().getPublicId();
        validateSalonOwnership(salonId);
        
        if (!"pending".equals(invitation.getStatus())) {
            throw new IllegalArgumentException("Can only resend pending invitations");
        }
        
        // Update expiration
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));
        staffInvitationRepository.save(invitation);
        
        // TODO: Send email
        // emailService.sendInvitationEmail(invitation.getEmail(), invitation.getInvitationToken().toString(), salonId);
    }
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    @Transactional
    public void cancelInvitation(UUID salonId, Long invitationId) {
        validateSalonOwnership(salonId);
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        
        StaffInvitation invitation = staffInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        
        if (!invitation.getSalon().getId().equals(saloon.getId())) {
            throw new IllegalArgumentException("Invitation does not belong to this salon");
        }
        
        if ("accepted".equals(invitation.getStatus())) {
            throw new IllegalArgumentException("Cannot cancel an accepted invitation");
        }
        
        invitation.setStatus("cancelled");
        staffInvitationRepository.save(invitation);
    }
    
    /**
     * Cancel invitation by invitationId only (extracts salonId from invitation)
     */
    @CacheEvict(value = "saloonsCache", allEntries = true)
    @Transactional
    public void cancelInvitationByInvitationId(Long invitationId) {
        StaffInvitation invitation = staffInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        
        // Validate ownership through the invitation's salon
        UUID salonId = invitation.getSalon().getPublicId();
        validateSalonOwnership(salonId);
        
        if ("accepted".equals(invitation.getStatus())) {
            throw new IllegalArgumentException("Cannot cancel an accepted invitation");
        }
        
        invitation.setStatus("cancelled");
        staffInvitationRepository.save(invitation);
    }
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    @Transactional
    public void setPaymentSettings(UUID salonId, PaymentSettingsDTO paymentSettings) {
        validateSalonOwnership(salonId);
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        
        // Validate payment settings
        validatePaymentSettings(paymentSettings);
        
        // Get or create payment settings
        Optional<PaymentSettings> existingSettings = paymentSettingsRepository.findBySalon(saloon);
        PaymentSettings settings;
        
        if (existingSettings.isPresent()) {
            settings = existingSettings.get();
        } else {
            settings = new PaymentSettings();
            settings.setSalon(saloon);
        }
        
        // Update payment methods
        settings.setCashAccepted(paymentSettings.getAcceptedMethods().getCash());
        settings.setCardAccepted(paymentSettings.getAcceptedMethods().getCard());
        settings.setOnlineAccepted(paymentSettings.getAcceptedMethods().getOnline());
        settings.setMobileAccepted(paymentSettings.getAcceptedMethods().getMobile());
        
        // Update deposit settings
        settings.setDepositRequired(paymentSettings.getDeposit().getRequired());
        settings.setDepositType(paymentSettings.getDeposit().getType());
        settings.setDepositAmount(paymentSettings.getDeposit().getAmount() != null ? 
            BigDecimal.valueOf(paymentSettings.getDeposit().getAmount()) : null);
        
        // Update cancellation settings
        settings.setCancellationAllowedUntil(paymentSettings.getCancellation().getAllowedUntil());
        settings.setCancellationFeeType(paymentSettings.getCancellation().getFee().getType());
        settings.setCancellationFeePercentage(paymentSettings.getCancellation().getFee().getAmount() != null ? 
            BigDecimal.valueOf(paymentSettings.getCancellation().getFee().getAmount()) : null);
        
        // Update no-show penalty
        settings.setNoShowPenalty(paymentSettings.getNoShow().getPenalty());
        
        paymentSettingsRepository.save(settings);
    }
    
    private void validatePaymentSettings(PaymentSettingsDTO settings) {
        // Validate deposit
        if (settings.getDeposit().getRequired() && settings.getDeposit().getAmount() <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than 0 when deposit is required");
        }
        if ("percentage".equals(settings.getDeposit().getType()) && 
            (settings.getDeposit().getAmount() < 0 || settings.getDeposit().getAmount() > 100)) {
            throw new IllegalArgumentException("Deposit percentage must be between 0 and 100");
        }
        
        // Validate cancellation fee
        if ("percentage".equals(settings.getCancellation().getFee().getType())) {
            if (settings.getCancellation().getFee().getAmount() == null ||
                settings.getCancellation().getFee().getAmount() < 0 ||
                settings.getCancellation().getFee().getAmount() > 100) {
                throw new IllegalArgumentException("Cancellation fee percentage must be between 0 and 100");
            }
        }
        
        // Validate no-show penalty
        if ("percentage".equals(settings.getNoShow().getPenalty()) &&
            settings.getNoShow().getAmount() != null) {
            if (settings.getNoShow().getAmount() < 0 || settings.getNoShow().getAmount() > 100) {
                throw new IllegalArgumentException("No-show penalty percentage must be between 0 and 100");
            }
        }
        
        // Check at least one payment method is enabled
        boolean hasPaymentMethod = settings.getAcceptedMethods().getCash() ||
                                   settings.getAcceptedMethods().getCard() ||
                                   settings.getAcceptedMethods().getOnline() ||
                                   settings.getAcceptedMethods().getMobile();
        if (!hasPaymentMethod) {
            throw new IllegalArgumentException("At least one payment method must be enabled");
        }
    }
    
    @CacheEvict(value = "saloonsCache", allEntries = true)
    @Transactional
    public void activateSalon(UUID salonId) {
        validateSalonOwnership(salonId);
        Saloon saloon = findSalonByPublicIdOrThrow(salonId);
        
        // TODO: Add status field to Saloon model and set it to "active"
        // For now, we'll just save the salon (assuming activation means it's ready)
        // In production, you'd have: saloon.setStatus("active");
        
        saloonRepository.save(saloon);
    }
}