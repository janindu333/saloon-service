package com.baber.saloonservice.service;

import com.baber.saloonservice.model.SaloonCategory;
import com.baber.saloonservice.repository.SaloonCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final SaloonCategoryRepository saloonCategoryRepository;
    
    public CategoryService(SaloonCategoryRepository saloonCategoryRepository) {
        this.saloonCategoryRepository = saloonCategoryRepository;
    }
    
    public void createSaloonCategory(SaloonCategory saloonCategory) {
        saloonCategoryRepository.save(saloonCategory);
    }
    
    public List<SaloonCategory> getCategories() {
        return saloonCategoryRepository.findAll();
    }
    
    public SaloonCategory getCategoryById(Long id) {
        Optional<SaloonCategory> category = saloonCategoryRepository.findById(id);
        return category.orElse(null);
    }
    
    public void updateCategory(Long id, SaloonCategory updatedCategory) {
        Optional<SaloonCategory> existingCategory = saloonCategoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            SaloonCategory category = existingCategory.get();
            category.setName(updatedCategory.getName());
            saloonCategoryRepository.save(category);
        } else {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
    }
    
    public void deleteCategory(Long id) {
        if (saloonCategoryRepository.existsById(id)) {
            saloonCategoryRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
    }
    
    public List<SaloonCategory> searchCategoriesByName(String name) {
        return saloonCategoryRepository.findByNameContainingIgnoreCase(name);
    }
    
    public SaloonCategory getCategoryByName(String name) {
        return saloonCategoryRepository.findByName(name);
    }
}
