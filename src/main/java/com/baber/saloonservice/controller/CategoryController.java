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

    @GetMapping("/getById/{id}")
    public BaseResponse<SaloonCategory> getCategoryById(@PathVariable Long id) {
        try {
            SaloonCategory category = categoryService.getCategoryById(id);
            if (category != null) {
                return new BaseResponse<>(true, "Success", 0, "", category);
            } else {
                return new BaseResponse<>(false, "Category not found", 1, "", null);
            }
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch category: " + e.getMessage(), 1, "", null);
        }
    }

    @PutMapping("/update/{id}")
    public BaseResponse<String> updateCategory(@PathVariable Long id, @RequestBody SaloonCategory category) {
        try {
            categoryService.updateCategory(id, category);
            return new BaseResponse<>(true, "Category updated successfully", 0, "", null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to update category: " + e.getMessage(), 1, "", null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return new BaseResponse<>(true, "Category deleted successfully", 0, "", null);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to delete category: " + e.getMessage(), 1, "", null);
        }
    }

    @GetMapping("/search")
    public BaseResponse<List<SaloonCategory>> searchCategories(@RequestParam String name) {
        try {
            List<SaloonCategory> categories = categoryService.searchCategoriesByName(name);
            return new BaseResponse<>(true, "Success", 0, "", categories);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to search categories: " + e.getMessage(), 1, "", null);
        }
    }

    @GetMapping("/getByName/{name}")
    public BaseResponse<SaloonCategory> getCategoryByName(@PathVariable String name) {
        try {
            SaloonCategory category = categoryService.getCategoryByName(name);
            if (category != null) {
                return new BaseResponse<>(true, "Success", 0, "", category);
            } else {
                return new BaseResponse<>(false, "Category not found", 1, "", null);
            }
        } catch (Exception e) {
            return new BaseResponse<>(false, "Failed to fetch category: " + e.getMessage(), 1, "", null);
        }
    }
}
