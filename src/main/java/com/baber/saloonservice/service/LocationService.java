package com.baber.saloonservice.service;

import com.baber.saloonservice.dto.LocationCreateDTO;
import com.baber.saloonservice.dto.LocationGetAllDTO;
import com.baber.saloonservice.model.Location;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.repository.LocationRepository;
import com.baber.saloonservice.repository.SaloonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final SaloonRepository saloonRepository;
    
    public LocationService(LocationRepository locationRepository, SaloonRepository saloonRepository) {
        this.locationRepository = locationRepository;
        this.saloonRepository = saloonRepository;
    }
    
    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location createLocation(LocationCreateDTO locationCreateDTO) {
        Location location = convertToLocation(locationCreateDTO);
        return locationRepository.save(location);
    }

    private Location convertToLocation(LocationCreateDTO dto) {
        Location location = new Location();
        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        location.setCity(dto.getCity());
        location.setState(dto.getState());
        location.setCountry(dto.getCountry());
        location.setPostalCode(dto.getPostalCode());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        location.setDescription(dto.getDescription());
        location.setTimezone(dto.getTimezone());
        location.setFormattedAddress(dto.getFormattedAddress());
        location.setPlaceId(dto.getPlaceId());
        return location;
    }
    
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public List<LocationGetAllDTO> getAllLocationsAsDTO() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
                .map(this::convertToGetAllDTO)
                .collect(Collectors.toList());
    }

    private LocationGetAllDTO convertToGetAllDTO(Location location) {
        LocationGetAllDTO dto = new LocationGetAllDTO();
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
    
    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }
    
    public Location updateLocation(Long id, Location location) {
        if (locationRepository.existsById(id)) {
            location.setId(id);
            return locationRepository.save(location);
        }
        throw new RuntimeException("Location not found with id: " + id);
    }
    
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
    
    public List<Location> getLocationsByCity(String city) {
        return locationRepository.findByCity(city);
    }

    public List<LocationGetAllDTO> getLocationsByCityAsDTO(String city) {
        List<Location> locations = locationRepository.findByCity(city);
        return locations.stream()
                .map(this::convertToGetAllDTO)
                .collect(Collectors.toList());
    }
    
    public List<Location> getLocationsByState(String state) {
        return locationRepository.findByState(state);
    }
    
    public List<Location> getLocationsByCountry(String country) {
        return locationRepository.findByCountry(country);
    }

    public List<LocationGetAllDTO> getLocationsByCountryAsDTO(String country) {
        List<Location> locations = locationRepository.findByCountry(country);
        return locations.stream()
                .map(this::convertToGetAllDTO)
                .collect(Collectors.toList());
    }
    
    public List<Location> searchLocationsByName(String name) {
        return locationRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Location> searchLocationsByAddress(String address) {
        return locationRepository.findByAddressContainingIgnoreCase(address);
    }
    
    public List<Location> getLocationsWithinDistance(double latitude, double longitude, double distance) {
        return locationRepository.findLocationsWithinDistance(latitude, longitude, distance);
    }
    
    // New methods for saloon-location relationship management
    public List<Location> getLocationsBySaloonId(Long saloonId) {
        Optional<Saloon> saloon = saloonRepository.findById(saloonId);
        if (saloon.isPresent()) {
            return saloon.get().getLocations();
        }
        return List.of(); // Return empty list if saloon not found
    }
    
    public Location getPrimaryLocationBySaloonId(Long saloonId) {
        Optional<Saloon> saloon = saloonRepository.findById(saloonId);
        if (saloon.isPresent()) {
            return saloon.get().getPrimaryLocation();
        }
        return null;
    }
    
    public void deleteLocationBySaloonId(Long saloonId) {
        Optional<Saloon> saloon = saloonRepository.findById(saloonId);
        if (saloon.isPresent()) {
            Saloon saloonEntity = saloon.get();
            // Clear primary location
            saloonEntity.setPrimaryLocation(null);
            // Clear all locations
            saloonEntity.getLocations().clear();
            saloonRepository.save(saloonEntity);
        }
    }
}
