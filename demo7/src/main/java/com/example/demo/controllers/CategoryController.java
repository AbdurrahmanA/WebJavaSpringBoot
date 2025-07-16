package com.example.demo.controllers;

import com.example.demo.data.CategoryEntity;
import com.example.demo.data.UserEntity;
import com.example.demo.services.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    private boolean hasAccess(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        return user != null &&
                (user.getRole() == UserEntity.Role.ROLE_ADMIN || user.getRole() == UserEntity.Role.ROLE_FULL_USER);
    }

    @GetMapping("/list")
    public String listCategories(HttpSession session, Model model) {
        if (!hasAccess(session)) {
            return "redirect:/access-denied";
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category-list";
    }

    @GetMapping("/add")
    public String showAddCategoryForm(HttpSession session, Model model) {
        if (!hasAccess(session)) {
            return "redirect:/access-denied";
        }
        model.addAttribute("category", new CategoryEntity());
        return "add-category";
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") CategoryEntity category,
                              BindingResult bindingResult,
                              HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/access-denied";
        }

        if (bindingResult.hasErrors()) {
            return "add-category";
        }

        categoryService.createCategory(category);
        return "redirect:/categories/list";
    }
}
