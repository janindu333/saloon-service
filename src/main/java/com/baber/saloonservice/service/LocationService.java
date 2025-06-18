package com.baber.saloonservice.service;

import com.baber.saloonservice.model.LocationWithSaloonId;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

}
