package com.baber.saloonservice.controller;

import com.baber.saloonservice.dto.BaseResponse;
import com.baber.saloonservice.dto.LocationCreateDTO;
import com.baber.saloonservice.dto.LocationGetAllDTO;
import com.baber.saloonservice.model.Location;
import com.baber.saloonservice.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/saloon/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }
    
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<String> createLocation(@RequestBody LocationCreateDTO locationCreateDTO) {
        try {
            locationService.createLocation(locationCreateDTO);
            return new BaseResponse<>(true, "Location created successfully", 0, "", null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to create location: " + e.getMessage(), 1, "", null);
        }
    }
    
    @GetMapping("/getAll")
    public BaseResponse<List<LocationGetAllDTO>> getAllLocations() {
        try {
            List<LocationGetAllDTO> locations = locationService.getAllLocationsAsDTO();
            return new BaseResponse<>(true, "Success", 0, "", locations);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch locations: " + e.getMessage(), 1, "", null);
        }
    }
    
    @GetMapping("/getById/{id}")
    public BaseResponse<Location> getLocationById(@PathVariable Long id) {
        try {
            Optional<Location> location = locationService.getLocationById(id);
            if (location.isPresent()) {
                return new BaseResponse<>(true, "Success", 0, "", location.get());
            } else {
                return new BaseResponse<>(false, "Location not found", 1, "", null);
            }
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch location: " + e.getMessage(), 1, "", null);
        }
    }
    
    @PutMapping("/update/{id}")
    public BaseResponse<String> updateLocation(@PathVariable Long id, @RequestBody Location location) {
        try {
            locationService.updateLocation(id, location);
            return new BaseResponse<>(true, "Location updated successfully", 0, "", null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to update location: " + e.getMessage(), 1, "", null);
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deleteLocation(@PathVariable Long id) {
        try {
            locationService.deleteLocation(id);
            return new BaseResponse<>(true, "Location deleted successfully", 0, "", null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to delete location: " + e.getMessage(), 1, "", null);
        }
    }
    
    @GetMapping("/getByCity/{city}")
    public BaseResponse<List<LocationGetAllDTO>> getLocationsByCity(@PathVariable String city) {
        try {
            List<LocationGetAllDTO> locations = locationService.getLocationsByCityAsDTO(city);
            return new BaseResponse<>(true, "Success", 0, "", locations);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch locations: " + e.getMessage(), 1, "", null);
        }
    }
    
    @GetMapping("/getByState/{state}")
    public BaseResponse<List<Location>> getLocationsByState(@PathVariable String state) {
        try {
            List<Location> locations = locationService.getLocationsByState(state);
            return new BaseResponse<>(true, "Success", 0, "", locations);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch locations: " + e.getMessage(), 1, "", null);
        }
    }
    
    @GetMapping("/getByCountry/{country}")
    public BaseResponse<List<LocationGetAllDTO>> getLocationsByCountry(@PathVariable String country) {
        try {
            List<LocationGetAllDTO> locations = locationService.getLocationsByCountryAsDTO(country);
            return new BaseResponse<>(true, "Success", 0, "", locations);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch locations: " + e.getMessage(), 1, "", null);
        }
    }
    
    @GetMapping("/searchByName")
    public BaseResponse<List<Location>> searchLocationsByName(@RequestParam String name) {
        try {
            List<Location> locations = locationService.searchLocationsByName(name);
            return new BaseResponse<>(true, "Success", 0, "", locations);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to search locations: " + e.getMessage(), 1, "", null);
        }
    }
    
    @GetMapping("/searchByAddress")
    public BaseResponse<List<Location>> searchLocationsByAddress(@RequestParam String address) {
        try {
            List<Location> locations = locationService.searchLocationsByAddress(address);
            return new BaseResponse<>(true, "Success", 0, "", locations);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to search locations: " + e.getMessage(), 1, "", null);
        }
    }
    
    @GetMapping("/getNearby")
    public BaseResponse<List<Location>> getLocationsWithinDistance(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double distance) {
        try {
            List<Location> locations = locationService.getLocationsWithinDistance(latitude, longitude, distance);
            return new BaseResponse<>(true, "Success", 0, "", locations);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch nearby locations: " + e.getMessage(), 1, "", null);
        }
    }
    
    // New endpoints for saloon-location relationship management
    @GetMapping("/getBySaloonId/{saloonId}")
    public BaseResponse<List<Location>> getLocationsBySaloonId(@PathVariable Long saloonId) {
        try {
            List<Location> locations = locationService.getLocationsBySaloonId(saloonId);
            return new BaseResponse<>(true, "Success", 0, "", locations);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch locations for saloon: " + e.getMessage(), 1, "", null);
        }
    }
    
    @GetMapping("/getPrimaryLocationBySaloonId/{saloonId}")
    public BaseResponse<Location> getPrimaryLocationBySaloonId(@PathVariable Long saloonId) {
        try {
            Location primaryLocation = locationService.getPrimaryLocationBySaloonId(saloonId);
            if (primaryLocation != null) {
                return new BaseResponse<>(true, "Success", 0, "", primaryLocation);
            } else {
                return new BaseResponse<>(false, "Primary location not found for saloon", 1, "", null);
            }
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch primary location: " + e.getMessage(), 1, "", null);
        }
    }
    
    @PutMapping("/updateLocation/{id}")
    public BaseResponse<String> updateLocationById(@PathVariable Long id, @RequestBody Location location) {
        try {
            locationService.updateLocation(id, location);
            return new BaseResponse<>(true, "Location updated successfully", 0, "", null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to update location: " + e.getMessage(), 1, "", null);
        }
    }
    
    @DeleteMapping("/deleteLocation/{saloonId}")
    public BaseResponse<String> deleteLocationBySaloonId(@PathVariable Long saloonId) {
        try {
            locationService.deleteLocationBySaloonId(saloonId);
            return new BaseResponse<>(true, "Location deleted successfully", 0, "", null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to delete location: " + e.getMessage(), 1, "", null);
        }
    }
}
