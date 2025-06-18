package com.baber.saloonservice.controller;

import com.baber.saloonservice.dto.BaseResponse;
import com.baber.saloonservice.model.LocationWithSaloonId;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.service.LocationService;
import com.baber.saloonservice.service.SaloonService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saloon/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

}
