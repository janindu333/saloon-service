package com.baber.saloonservice.service;

import com.baber.saloonservice.dto.*;
import com.baber.saloonservice.model.*;
import com.baber.saloonservice.model.Location;
import com.baber.saloonservice.repository.SaloonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SaloonService {

    private final SaloonRepository saloonRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final double EARTH_RADIUS = 6371; // Earth's radius in kilometers

    @Autowired
    public SaloonService(SaloonRepository saloonRepository, RedisTemplate<String, Object> redisTemplate) {
        this.saloonRepository = saloonRepository;
        this.redisTemplate = redisTemplate;
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
    public void createSaloon(SaloonCreateDTO saloonCreateDTO) {
        Saloon saloon = convertToSaloon(saloonCreateDTO);
        saloonRepository.save(saloon);
    }

    private Saloon convertToSaloon(SaloonCreateDTO dto) {
        Saloon saloon = new Saloon();
        saloon.setName(dto.getName());
        saloon.setAddress(dto.getAddress());
        saloon.setRating(dto.getRating());
        saloon.setNoOfReviews(dto.getNoOfReviews());
        saloon.setImageUrl(dto.getImageUrl());
        saloon.setUserLiked(dto.isUserLiked());
        saloon.setPhoneNumber(dto.getPhoneNumber());
        saloon.setDescription(dto.getDescription());
        saloon.setOpeningDays(dto.getOpeningDays());
        saloon.setOpenTime(dto.getOpenTime());
        saloon.setCloseTime(dto.getCloseTime());
        
        // Convert primary location if provided
        if (dto.getPrimaryLocation() != null) {
            Location location = convertToLocation(dto.getPrimaryLocation());
            saloon.setPrimaryLocation(location);
        }
        
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
}