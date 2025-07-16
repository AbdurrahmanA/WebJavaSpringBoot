package com.example.demo.repositories;

import com.example.demo.data.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryEntityRepository extends JpaRepository<CategoryEntity, Integer> {
}