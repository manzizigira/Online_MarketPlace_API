package com.ecommerce.Ecommerce.Web.implementation;

import com.ecommerce.Ecommerce.Web.entity.Categories;
import com.ecommerce.Ecommerce.Web.entity.Orders;
import com.ecommerce.Ecommerce.Web.repository.CategoriesRepository;
import com.ecommerce.Ecommerce.Web.service.CategoryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryImplementation implements CategoryService {

    private CategoriesRepository categoriesRepository;

    public CategoryImplementation(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public List<Categories> viewAllCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public Categories createCategory(Categories categories) {
        return categoriesRepository.save(categories);
    }

    @Override
    public Categories getCategoryById(Long categoryId) {

        Optional<Categories> getCategoryId = categoriesRepository.findById(categoryId);

        Categories category = null;

        if (getCategoryId.isPresent()){
            category = getCategoryId.get();
        }else{
            throw new RuntimeException("Category not found");
        }
        return category;
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        categoriesRepository.deleteById(categoryId);
    }

    @Override
    public List<Categories> getCategoriesByName(String categoryName) {
        return categoriesRepository.findByCategoryName(categoryName);
    }

    @Override
    public boolean updateCategoryFields(Categories categories) {
        Optional<Categories> existingCategoryOpt = categoriesRepository.findById(categories.getId());

        if (existingCategoryOpt.isPresent()) {
            Categories existingCategory = existingCategoryOpt.get();

            // Update only specific fields

            // Check if totalQuantity is not null and not zero
            if (categories.getCategoryName() != null && !categories.getCategoryName().isEmpty()) {
                existingCategory.setCategoryName(categories.getCategoryName());
            }


            // Save the updated user back to the database
            categoriesRepository.save(existingCategory);
            return true;
        }
        return false;
    }
}
