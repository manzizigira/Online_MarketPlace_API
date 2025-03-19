package com.ecommerce.Ecommerce.Web.repository;

import com.ecommerce.Ecommerce.Web.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Long> {

    Page<Products> findByCategoriesId(@Param("id") Long id, Pageable pageable);

    Page<Products> findByNameContaining(@Param("name") String name, Pageable page);

    List<Products> findByCategoriesId(Long categoryId);

    List<Products> findByFeaturedTrue();

    List<Products> findByCategories_CategoryNameIgnoreCase(String categoryName);

    List<Products> findByTagsContainingIgnoreCase(String tag); // Fetch by tags

    List<Products> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description); // Search
}
