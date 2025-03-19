package com.ecommerce.Ecommerce.Web.service;

import com.ecommerce.Ecommerce.Web.entity.Categories;

import java.util.List;

public interface CategoryService {

    List<Categories> viewAllCategories();

    Categories createCategory(Categories categories);

    Categories getCategoryById(Long categoryId);

    void deleteCategoryById(Long categoryId);

    List<Categories> getCategoriesByName(String categoryName);

    boolean updateCategoryFields(Categories categories);
}
