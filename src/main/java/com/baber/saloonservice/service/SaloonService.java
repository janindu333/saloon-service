package com.baber.saloonservice.service;

import com.baber.saloonservice.dto.OfferRequest;
import com.baber.saloonservice.dto.SaloonDTO;
import com.baber.saloonservice.model.LocationWithSaloonId;
import com.baber.saloonservice.model.OfferItems;
import com.baber.saloonservice.model.Saloon;
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
        // Set other fields as needed
        return dto;
    }

    public Optional<Saloon> findById(Long id) {
        return saloonRepository.findById(id);
    }

    public Set<Long> findSaloonIdsBySpecialistId(Long id) {
        return saloonRepository.findSaloonIdsBySpecialistId(id);
    }

    public boolean createLocationWithSaloonId(LocationWithSaloonId locationWithSaloonId) {
        Optional<Saloon> optionalSaloon = saloonRepository.findById(locationWithSaloonId.getSaloonId());
        if (optionalSaloon.isEmpty()) {
            return false;
        }
        Saloon saloon = optionalSaloon.get();
        saloon.setLatitude(locationWithSaloonId.getLatitude());
        saloon.setLongitude(locationWithSaloonId.getLongitude());
        saloonRepository.save(saloon);
        return true;
    }

    public List<Saloon> getNearBySaloons(String userLatitudeReq, String userLongitudeReq, String distanceLimitReq) {
        double userLatitude = Double.parseDouble(userLatitudeReq);
        double userLongitude = Double.parseDouble(userLongitudeReq);
        double distanceLimit = Double.parseDouble(distanceLimitReq);

        List<Saloon> allSaloons = saloonRepository.findAll();
        List<Saloon> nearbySaloons = new ArrayList<>();

        for (Saloon saloon : allSaloons) {
            double saloonLatitude = Double.parseDouble(saloon.getLatitude());
            double saloonLongitude = Double.parseDouble(saloon.getLongitude());
            double distance = calculateDistance(userLatitude, userLongitude, saloonLatitude, saloonLongitude);
            if (distance <= distanceLimit) {
                nearbySaloons.add(saloon);
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
}
