package com.ecommerce.Ecommerce.Web.repository;

import com.ecommerce.Ecommerce.Web.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    // find a category name
    List<Categories> findByCategoryName(String categoryName);
}
