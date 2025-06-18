package com.baber.saloonservice.controller;

import com.baber.saloonservice.dto.*;
import com.baber.saloonservice.model.LocationWithSaloonId;
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
    public BaseResponse<String> createSaloon(@RequestBody Saloon saloon) {
        try {
            saloonService.createSaloon(saloon); // This automatically evicts the cache

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

    @PutMapping("/addCurrentLocation")
    public BaseResponse<String> addLocatonWithSaloonId(@RequestBody LocationWithSaloonId locationWithSaloonId) {

        if (saloonService.createLocationWithSaloonId(locationWithSaloonId)) {
            return new BaseResponse<>(true, "Success", 0, "", null);
        } else {
            return new BaseResponse<>(false, "Failure: ", 1, "", null);
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
