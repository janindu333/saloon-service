package com.baber.saloonservice.controller;

import com.baber.saloonservice.configurations.UserContext;
import com.baber.saloonservice.dto.BaseResponse;
import com.baber.saloonservice.dto.SaloonServiceRequest;
import com.baber.saloonservice.dto.SaloonServiceTypeRequest;
import com.baber.saloonservice.model.SaloonServiceType;
import com.baber.saloonservice.model.SaloonServices;
import com.baber.saloonservice.service.SaloonServicesService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saloon/services")
public class SaloonServicesController {
    private final SaloonServicesService saloonServicesService;

    public SaloonServicesController(SaloonServicesService saloonServicesService) {
        this.saloonServicesService = saloonServicesService;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> createSaloonService(@RequestBody SaloonServiceRequest saloonServiceRequest) {

        try {
            saloonServicesService.createSaloonService(saloonServiceRequest);
            return new BaseResponse<>(true, "Success", 0, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure: " + e.getMessage(), 1, "", null);
        }
    }
    @GetMapping(value = "/getall/{saloonServiceId}")
    public BaseResponse<SaloonServices> getSaloonServices(@PathVariable Long saloonServiceId) {
        try {
            if (saloonServicesService.getSaloonServicesWithTypes(saloonServiceId).getName().isEmpty()) {
                return new BaseResponse<>(true, "Failure", 0, "No saloon service type related to given id", null);
            } else {
                return new BaseResponse<>(true, "Success", 0, "", saloonServicesService.getSaloonServicesWithTypes(saloonServiceId));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failure: " + e.getMessage(), 1, "", null);
        }
    }
    @DeleteMapping(value = "/deleteservicetype/{serviceTypeId}")
    public BaseResponse<String> deleteServiceTypeFromService(@PathVariable Long serviceTypeId) {

        try {
            saloonServicesService.deleteServiceTypeFromService(serviceTypeId);
            return new BaseResponse<>(true, "Success", 0, "", null);
        } catch (EmptyResultDataAccessException e) {
            // The specified id doesn't exist in the database
            return new BaseResponse<>(false, null, 0, "Service type is not available", null);
        } catch (Exception e) {
            // Other error occurred
            return new BaseResponse<>(false, null, 0, "Failed to delete, please try again later", null);
        }
    }

    @PostMapping(value = "/createServiceType")
    public BaseResponse<String> addServiceTypesToSaloonService(@RequestBody SaloonServiceTypeRequest
                                                                       saloonServiceTypeRequest)
            throws Exception {
        try {

          //  System.out.println(userAgent);
            // Set userDetailsJson in the UserContext
        //    UserContext.setUserDetailsJson(userAgent);

            return new BaseResponse<>(true, "Success", 0, "",
                    saloonServicesService.addServiceTypesToSaloonService(saloonServiceTypeRequest));
        } catch (Exception e) {
            return new BaseResponse<>(false, "failed", 0,
                    saloonServicesService.addServiceTypesToSaloonService(saloonServiceTypeRequest), null);
        } finally {
            // Clear userDetailsJson from the UserContext to avoid potential memory leaks
            UserContext.clear();
        }
    }

    @PutMapping(value = "/updateService")
    public BaseResponse<String> updateServiceType(@RequestBody SaloonServices
                                                          saloonService) {

        if (saloonServicesService.updateSaloonServices(saloonService) != null) {
            return new BaseResponse<>(true, "update Success", 0, "", null
            );
        } else {
            return new BaseResponse<>(false, null, 0, "update failed", null
            );
        }
    }
    @PutMapping(value = "/updateServiceType")
    public BaseResponse<String> updateServiceType(@RequestBody SaloonServiceType
                                                          saloonServiceType) {

        if (saloonServicesService.updateSaloonServiceType(saloonServiceType) != null) {
            return new BaseResponse<>(true, "update Success", 0, "", null
            );
        } else {
            return new BaseResponse<>(false, null, 0, "update failed", null
            );
        }
    }

    @GetMapping("/getBySaloonId/{saloonId}")
    public BaseResponse<List<SaloonServices>> getServicesBySaloonId(@PathVariable Long saloonId) {
        try {
            List<SaloonServices> services = saloonServicesService.getServicesBySaloonId(saloonId);
            return new BaseResponse<>(true, "Success", 0, "", services);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failed to fetch services: " + e.getMessage(), 1, "", null);
        }
    }

    @GetMapping("/getServiceTypesByServiceId/{serviceId}")
    public BaseResponse<List<SaloonServiceType>> getServiceTypesByServiceId(@PathVariable Long serviceId) {
        try {
            List<SaloonServiceType> serviceTypes = saloonServicesService.getServiceTypesByServiceId(serviceId);
            return new BaseResponse<>(true, "Success", 0, "", serviceTypes);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse<>(false, "Failed to fetch service types: " + e.getMessage(), 1, "", null);
        }
    }
}
