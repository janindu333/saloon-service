package com.baber.saloonservice.service;

import com.baber.saloonservice.model.SaloonCategory;
import com.baber.saloonservice.repository.SaloonCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
