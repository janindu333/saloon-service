package com.baber.saloonservice.controller;

import com.baber.saloonservice.dto.*;
import com.baber.saloonservice.model.OfferItems;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.service.SaloonService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

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
    public BaseResponse<String> createSaloon(@RequestBody SaloonCreateDTO saloonCreateDTO) {
        try {
            saloonService.createSaloon(saloonCreateDTO); // This automatically evicts the cache

            return new BaseResponse<>(true, "Success", 0, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure: " + e.getMessage(), 1, "", null);
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
}