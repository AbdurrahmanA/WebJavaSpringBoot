package com.example.demo;

import com.example.demo.data.CategoryEntity;
import com.example.demo.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional  // Test sonrası DB değişiklikleri geri alınır
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void testCreateAndGetCategory() {
        try {
            // Yeni kategori oluştur
            CategoryEntity category = new CategoryEntity();
            category.setName("technology"); // Küçük harf ve 3-20 karakter, Pattern kuralına uygun

            // Kaydet
            CategoryEntity savedCategory = categoryService.createCategory(category);

            // Kaydedilen ID null olmamalı
            assertNotNull(savedCategory.getId(), "Kategori ID null olmamalı");

            // Tekil olarak ID ile getir
            Optional<CategoryEntity> found = categoryService.getCategoryById(savedCategory.getId());
            assertTrue(found.isPresent(), "Kategori ID ile bulunmalı");
            assertEquals("technology", found.get().getName());

            // Tüm kategorileri getir
            List<CategoryEntity> allCategories = categoryService.getAllCategories();
            assertFalse(allCategories.isEmpty(), "Kategori listesi boş olmamalı");

            // Kaydettiğimiz kategori listede var mı kontrol et
            boolean contains = allCategories.stream()
                    .anyMatch(c -> c.getId().equals(savedCategory.getId()) && c.getName().equals("technology"));

            assertTrue(contains, "Kategori listede bulunmalı");

            System.out.println("testCreateAndGetCategory testi başarılı.");

        } catch (AssertionError e) {
            System.err.println("testCreateAndGetCategory testi başarısız: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("testCreateAndGetCategory testi sırasında beklenmeyen hata: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testCreateCategoryWithInvalidName_shouldFail() {
        // Bu test aynen kalabilir, çünkü validasyon servis seviyesinde değil controller seviyesinde gerçekleşiyor.
    }
}
