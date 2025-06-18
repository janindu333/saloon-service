package com.baber.saloonservice.service;

import com.baber.saloonservice.dto.SaloonServiceRequest;
import com.baber.saloonservice.dto.SaloonServiceTypeRequest;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.model.SaloonServiceType;
import com.baber.saloonservice.model.SaloonServices;
import com.baber.saloonservice.repository.SaloonRepository;
import com.baber.saloonservice.repository.SaloonServiceRepository;
import com.baber.saloonservice.repository.SaloonServiceTypeRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SaloonServicesService {
    private final SaloonServiceRepository saloonServiceRepository;
    private final SaloonRepository saloonRepository;

    private final SaloonServiceTypeRepository saloonServiceTypeRepository;
    @Autowired
    public SaloonServicesService(SaloonServiceRepository saloonServiceRepository,
                                 SaloonRepository saloonRepository , SaloonServiceTypeRepository saloonServiceTypeRepository) {
        this.saloonServiceRepository = saloonServiceRepository;
        this.saloonRepository = saloonRepository;
        this.saloonServiceTypeRepository = saloonServiceTypeRepository;
    }

    public String createSaloonService(SaloonServiceRequest saloonServiceRequest) throws Exception {
        Optional<Saloon> saloonOptional = saloonRepository.findById(saloonServiceRequest.getSaloonId());

        if (saloonOptional.isPresent()) {
            Saloon saloon = saloonOptional.get();

            SaloonServices saloonServices = new SaloonServices();
            saloonServices.setName(saloonServiceRequest.getName());
            saloonServices.setSaloon(saloon);

            List<SaloonServiceType> serviceTypes = new ArrayList<>();

            for (SaloonServiceType typeRequest : saloonServiceRequest.getSaloonServiceType()) {
                SaloonServiceType serviceType = new SaloonServiceType();
                serviceType.setName(typeRequest.getName());
                serviceType.setDuration(typeRequest.getDuration());
                serviceType.setDescription(typeRequest.getDescription());
                serviceType.setCost(typeRequest.getCost());

                // Add the serviceType to the saloonServices entity using the helper method
                //  saloonServices.addSaloonServiceType(serviceType);

                serviceTypes.add(serviceType);
            }

            //    saloonServices.setSaloonServiceType(serviceTypes);

            // Save the SaloonServices object to persist it along with its associated SaloonServiceType records
            saloonServiceRepository.save(saloonServices);

            return "Saloon service successfully created.";
        } else {
            return "Saloon not found  ";
        }
    }

    public List<SaloonServices> getAllSaloons() {
        try {

            return saloonServiceRepository.findAll();
        } catch (Exception e) {
            // Handle the exception or rethrow it as needed
            e.printStackTrace(); // Print the stack trace for debugging
            // You can also log the error or return an error response to the user
            return null;
        }
    }
    public SaloonServices getSaloonServicesWithTypes(Long saloonServiceId) {
        Optional<SaloonServices> saloonOptional = saloonServiceRepository.findById(saloonServiceId);

        return saloonOptional.get();
    }
    public void deleteServiceTypeFromService(Long id) {
        saloonServiceTypeRepository.delete(saloonServiceTypeRepository.findById(id).get());
    }
    public String addServiceTypesToSaloonService(SaloonServiceTypeRequest saloonServiceTypeRequest) throws Exception {
        Optional<SaloonServices> saloonServicesOptional = saloonServiceRepository.findById(saloonServiceTypeRequest.getServiceId());

        if (saloonServicesOptional.isPresent()) {
            SaloonServiceType serviceType = new SaloonServiceType();
            serviceType.setName(saloonServiceTypeRequest.getName());
            serviceType.setDuration(saloonServiceTypeRequest.getDuration());
            serviceType.setDescription(saloonServiceTypeRequest.getDescription());
            serviceType.setCost(saloonServiceTypeRequest.getCost());
            serviceType.setSaloonService(saloonServicesOptional.get());
            saloonServiceTypeRepository.save(serviceType);
            return "Saloon service type successfully created.";

        } else {
            return "Saloon Service not found  ";
        }
    }
    @Transactional
     public SaloonServiceType  updateSaloonServiceType(SaloonServiceType updatedServiceType) {

        // Check if the service type exists in the database
        Long id = updatedServiceType.getId();
        SaloonServiceType existingServiceType = saloonServiceTypeRepository.findById(id).orElse(null);

        if (existingServiceType == null) {
            return null;
        } else {

            // Update the fields with the new values
            existingServiceType.setName(updatedServiceType.getName());
            existingServiceType.setDuration(updatedServiceType.getDuration());
            existingServiceType.setDescription(updatedServiceType.getDescription());
            existingServiceType.setCost(updatedServiceType.getCost());

            // Save the updated service type to the database
            return saloonServiceTypeRepository.save(existingServiceType);
        }
    }
    public SaloonServices updateSaloonServices(SaloonServices updatedServices) {
        // Check if the services exist in the database
        Long id = updatedServices.getId();
        SaloonServices existingServices = saloonServiceRepository.findById(id).orElse(null);

        if (existingServices == null) {
            return null;
        }

        // Update the fields with the new values
        existingServices.setName(updatedServices.getName());

        // Save the updated services to the database
        return saloonServiceRepository.save(existingServices);
    }

}
