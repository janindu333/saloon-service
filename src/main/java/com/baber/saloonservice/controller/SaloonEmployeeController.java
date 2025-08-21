package com.baber.saloonservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.baber.saloonservice.dto.BaseResponse;
import com.baber.saloonservice.dto.SpecialistToSaloonRequest;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.model.SaloonSpecialist;
import com.baber.saloonservice.dto.SaloonSpecialistDTO;
import com.baber.saloonservice.model.SaloonSpecialistHairStyleImages;
import com.baber.saloonservice.service.SaloonService;
import com.baber.saloonservice.service.SaloonSpecialistService;
@RequestMapping("/api/saloon/emp")
@RestController
public class SaloonEmployeeController {
    private final SaloonSpecialistService saloonSpecialistService;
    private final SaloonService saloonService;
    public SaloonEmployeeController(SaloonSpecialistService saloonSpecialistService, SaloonService saloonService) {
        this.saloonSpecialistService = saloonSpecialistService;
        this.saloonService = saloonService;
    }
    @PostMapping(value = "/create")
     public BaseResponse<String> createSaloonSpecialist(@RequestBody SaloonSpecialist saloonSpecialist) {
        try {
            saloonSpecialistService.createSaloonSpecialist(saloonSpecialist);
            return new BaseResponse<>(true, "Success", 0, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure: " + e.getMessage(), 1, "", null);
        }
    }
    @GetMapping("/getAll")
    public BaseResponse<List<SaloonSpecialistDTO>> getSaloonSpecialists() {

        List<SaloonSpecialist> specialists = saloonSpecialistService.getSaloonSpecialists();
        List<SaloonSpecialistDTO> specialistDTOs = new ArrayList<>();

        for (SaloonSpecialist specialist : specialists) {
//            Set<Long> saloonIds = specialist.getSaloons()
//                    .stream()
//                    .map(Saloon::getId)
//                    .collect(Collectors.toSet());

            Set<Long> saloonIds = saloonService.findSaloonIdsBySpecialistId(specialist.getId());

            Set<Long> idSaloonSpecialistImages = specialist.getHairstyleImages()
                    .stream()
                    .map(SaloonSpecialistHairStyleImages::getId)
                    .collect(Collectors.toSet());

            SaloonSpecialistDTO specialistDTO = new SaloonSpecialistDTO();
            specialistDTO.setId(specialist.getId());
            specialistDTO.setName(specialist.getName());
            specialistDTO.setPossition(specialist.getPossition());
            specialistDTO.setImage(specialist.getImage());
            specialistDTO.setNumber(specialist.getNumber());
            specialistDTO.setAbout(specialist.getAbout());
            specialistDTO.setSaloonIds(saloonIds);
            specialistDTO.setHairstyleImageIds(idSaloonSpecialistImages);
            specialistDTOs.add(specialistDTO);
        }

        return new BaseResponse<>(true, "success", 0, "", specialistDTOs);
    }

    @GetMapping("/getSpecialistBySaloonId")
    public BaseResponse<List<SaloonSpecialist>> getSaloonSpecialistsBySaloonId(@RequestParam Long saloonId) {

        List<SaloonSpecialist> specialists = saloonSpecialistService.getSaloonSpecialistsBySaloonId(saloonId);

        return new BaseResponse<>(true, "success", 0, "", specialists);
    }

    @PostMapping("/addImageToSpecialist/{id}")
    public BaseResponse<String> addImageToSaloonSpecialist(@PathVariable("id") Long id) {
        try {
            saloonSpecialistService.addImageToSaloonSpecialist(id);
            return new BaseResponse<>(true, "Success", 0, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure: " + e.getMessage(), 1, "", null);
        }
    }
    @PostMapping("/addSpecialistToSaloon")
    public BaseResponse<String> addSpecialistToSaloon(@RequestBody SpecialistToSaloonRequest specialistToSaloonRequest) {
        try {

            Long saloonId = specialistToSaloonRequest.getSaloonId();
            Long specialistId = specialistToSaloonRequest.getSpecialistId();

            Optional<Saloon> optionalSaloon = saloonService.findById(saloonId);
            Optional<SaloonSpecialist> optionalSaloonSpecialist = saloonSpecialistService.findById(specialistId);

            if (optionalSaloon.isPresent()) {
                if (optionalSaloonSpecialist.isPresent()) {
                    Saloon saloon = optionalSaloon.get();
                    List<SaloonSpecialist> specialists = saloon.getSpecialists();
                    specialists.add(optionalSaloonSpecialist.get());

                    // Associate the specialist with the saloon
                    //                specialist.getSaloons().add(saloon);

                    // Update the saloon record
                //    saloonService.createSaloon(saloon);

                    return new BaseResponse<>(true, "Success", 0, "", null);
                } else {
                    return new BaseResponse<>(false, "Saloon specialist not found", 1, "", null);
                }
            } else {
                return new BaseResponse<>(false, "Saloon not found", 1, "", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure: " + e.getMessage(), 1, "", null);
        }
    }
    @PostMapping("/removeSpecialistFromSaloon")
    public BaseResponse<String> removeSpecialistFromSaloon(
            @RequestBody SpecialistToSaloonRequest specialistToSaloonRequest) {
        try {
            Long saloonId = specialistToSaloonRequest.getSaloonId();

            Long specialistId = specialistToSaloonRequest.getSpecialistId();

            Optional<Saloon> optionalSaloon = saloonService.findById(saloonId);

            Optional<SaloonSpecialist> optionalSaloonSpecialist = saloonSpecialistService.findById(specialistId);

            if (optionalSaloon.isPresent()) {
                if (optionalSaloonSpecialist.isPresent()) {
                    Saloon saloon = optionalSaloon.get();
                    List<SaloonSpecialist> specialists = saloon.getSpecialists();
                    specialists.remove(optionalSaloonSpecialist.get());

                    // Update the saloon record
                 //   saloonService.createSaloon(saloon);

                    return new BaseResponse<>(true, "Success", 0, "", null);
                }else {
                    return new BaseResponse<>(false, "Saloon specialist not found", 1, "", null);
                }
            } else {
                return new BaseResponse<>(false, "Saloon not found", 1, "", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure: " + e.getMessage(), 1, "", null);
        }
    }

    @GetMapping("/getById/{id}")
    public BaseResponse<SaloonSpecialist> getSpecialistById(@PathVariable Long id) {
        try {
            SaloonSpecialist specialist = saloonSpecialistService.getSpecialistById(id);
            if (specialist != null) {
                return new BaseResponse<>(true, "Success", 0, "", specialist);
            } else {
                return new BaseResponse<>(false, "Specialist not found", 1, "", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failed to fetch specialist: " + e.getMessage(), 1, "", null);
        }
    }

    @PutMapping("/update/{id}")
    public BaseResponse<String> updateSpecialist(@PathVariable Long id, @RequestBody SaloonSpecialist specialist) {
        try {
            saloonSpecialistService.updateSpecialist(id, specialist);
            return new BaseResponse<>(true, "Specialist updated successfully", 0, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failed to update specialist: " + e.getMessage(), 1, "", null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deleteSpecialist(@PathVariable Long id) {
        try {
            saloonSpecialistService.deleteSpecialist(id);
            return new BaseResponse<>(true, "Specialist deleted successfully", 0, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failed to delete specialist: " + e.getMessage(), 1, "", null);
        }
    }

    @GetMapping("/getSpecialistsByService/{serviceId}")
    public BaseResponse<List<SaloonSpecialist>> getSpecialistsByService(@PathVariable Long serviceId) {
        try {
            List<SaloonSpecialist> specialists = saloonSpecialistService.getSpecialistsByService(serviceId);
            return new BaseResponse<>(true, "Success", 0, "", specialists);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failed to fetch specialists by service: " + e.getMessage(), 1, "", null);
        }
    }
}
