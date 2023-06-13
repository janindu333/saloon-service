package com.baber.saloonservice.controller;

import com.baber.saloonservice.dto.BaseResponse;
import com.baber.saloonservice.dto.SaloonRequest;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.service.SaloonService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saloon")
public class SaloonController {

    private final SaloonService saloonService;

    public SaloonController(SaloonService saloonService) {
        this.saloonService = saloonService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<String> createSaloon(@RequestBody Saloon saloon) {
        saloonService.createSaloon(saloon);
        return new BaseResponse<>(true, "success", 0,"",null);
    }

    @GetMapping
    public BaseResponse<List<Saloon>> getSaloons(){
        return new BaseResponse<>(true, "success", 0,"",saloonService.getSaloons());

    }

    @GetMapping("/test")
    public BaseResponse<String> getTest(){

        return new BaseResponse<>(true, "success", 0,"",saloonService.test());

    }

}
