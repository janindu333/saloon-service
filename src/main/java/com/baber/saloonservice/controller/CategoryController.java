package com.baber.saloonservice.controller;

import com.baber.saloonservice.dto.BaseResponse;
import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.model.SaloonCategory;
import com.baber.saloonservice.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saloon/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public BaseResponse<List<SaloonCategory>> getCategories() {

        return new BaseResponse<>(true, "success", 0, "", categoryService.getCategories());
    }

    @PostMapping
    public BaseResponse<String> createCategory(@RequestBody SaloonCategory saloonCategory) {
        categoryService.createSaloonCategory(saloonCategory);
        return new BaseResponse<>(true, "success", 0, "", null);
    }
}
