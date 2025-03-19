package com.ecommerce.Ecommerce.Web.controller;

import com.ecommerce.Ecommerce.Web.entity.Categories;
import com.ecommerce.Ecommerce.Web.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/viewCategoryPage")
    public String getCatPage(Model model){
        List<Categories> fetchAll = categoryService.viewAllCategories();
        model.addAttribute("categories", fetchAll);
        model.addAttribute("newCategory", new Categories());
        return "add-category";
    }

    @GetMapping("/viewCategories")
    private List<Categories> viewAllCategories(){
        return categoryService.viewAllCategories();
    }

    @PostMapping("/registerCategory")
    public String registerCategories(@ModelAttribute Categories categories){
        categoryService.createCategory(categories);
        return "redirect:/viewCategoryPage";
    }

    @PostMapping("/update-category")
    public String updateUser(@ModelAttribute Categories categories, Model model) {
        // Call service to update only the required fields of the user
        boolean updated = categoryService.updateCategoryFields(categories);

        if (updated) {
            model.addAttribute("message", "Category updated successfully");
        } else {
            model.addAttribute("message", "Failed to update Category");
        }

        return "redirect:/viewCategoryPage";  // Redirect to user details page
    }

    @PostMapping("/update-categories")
    public String showUpdateForm(@RequestParam("id") Long categoryId, Model model) {
        Categories categories = categoryService.getCategoryById(categoryId);

        if (categories != null) {
            model.addAttribute("category", categories);
            return "update-category";
        } else {
            return "redirect:/viewCategoryPage";
        }
    }

    @PostMapping("/delete-category")
    public String deleteProduct(@RequestParam("id") Long productId) {
        try {
            categoryService.deleteCategoryById(productId); // Call the delete service method
            return "redirect:/viewCategoryPage"; // Redirect to the product list page
        } catch (NumberFormatException e) {
            // Handle invalid id format (though this won't happen with Long type)
            return "redirect:/viewCategoryPage"; // Handle errors gracefully
        }
    }

}
