package com.baber.saloonservice.service;

import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.repository.SaloonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class SaloonService {

    private final SaloonRepository saloonRepository;
private final WebClient.Builder webclientbBuilder;

    @Autowired
    public SaloonService(SaloonRepository saloonRepository, WebClient.Builder webclientbBuilder) {
        this.saloonRepository = saloonRepository;
        this.webclientbBuilder = webclientbBuilder;
    }

    public void createSaloon(Saloon saloon) {

        saloonRepository.save(saloon);
    }

    public List<Saloon> getSaloons() {

        return saloonRepository.findAll();
    }

    public String test() {

        return webclientbBuilder.build().get().uri("http://appointment-service/api/saloon/appointment/test").retrieve().bodyToMono(String.class).block();

    }

}
