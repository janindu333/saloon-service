package com.baber.saloonservice.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.repository.SaloonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baber.saloonservice.model.SaloonSpecialist;
import com.baber.saloonservice.model.SaloonSpecialistHairStyleImages;
import com.baber.saloonservice.repository.SaloonSpecialistRepository;

@Service
public class SaloonSpecialistService {

    private final SaloonSpecialistRepository saloonSpecialistRepository;
    private final SaloonRepository saloonRepository;
    @Autowired
    public SaloonSpecialistService(SaloonSpecialistRepository saloonSpecialistRepository , SaloonRepository
            saloonRepository) {
        this.saloonSpecialistRepository = saloonSpecialistRepository;
        this.saloonRepository = saloonRepository;
    }
    public void createSaloonSpecialist(SaloonSpecialist saloonSpecialist) {
        try {
            saloonSpecialistRepository.save(saloonSpecialist);
        } catch (Exception e) {
            // Handle the exception
            // You can log the error, throw a custom exception, or take other appropriate
            // actions
            e.printStackTrace();
            throw new RuntimeException("Failed to create saloon specialist: " + e.getMessage());
        }
    }
    public List<SaloonSpecialist> getSaloonSpecialists() {

        return saloonSpecialistRepository.findAll();
    }

    public List<SaloonSpecialist> getSaloonSpecialistsBySaloonId(Long saloonId) {
        // Retrieve the saloon by ID
        Saloon saloon = saloonRepository.findById(saloonId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Saloon ID"));

        // Get the specialists associated with the saloon
        return saloon.getSpecialists();
    }

    public void addImageToSaloonSpecialist(Long specialistId) {
        // Assuming you have the SaloonSpecialist's ID and the
        // SaloonSpecialistHairStyleImages details
        // Replace with the actual ID of the SaloonSpecialist
        SaloonSpecialistHairStyleImages hairstyleImage = new SaloonSpecialistHairStyleImages();
        hairstyleImage.setSaloonSpecialistImageUrl("image_url_here");

        // Retrieve the SaloonSpecialist from the database
        SaloonSpecialist specialist = saloonSpecialistRepository.findById(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid SaloonSpecialist ID"));

        // Associate the hairstyleImage with the specialist
        hairstyleImage.setSaloonSpecialist(specialist);
      //  specialist.getHairstyleImages().add(hairstyleImage);

        // Save the changes to the database
        saloonSpecialistRepository.save(specialist);

    }

    public Optional<SaloonSpecialist> findById(Long id) {

        return saloonSpecialistRepository.findById(id);
    }

}
