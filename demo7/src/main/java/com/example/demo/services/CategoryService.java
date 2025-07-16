package com.example.demo.services;

import com.example.demo.data.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryEntity createCategory(CategoryEntity category);
    List<CategoryEntity> getAllCategories();
    Optional<CategoryEntity> getCategoryById(Integer id);
}
