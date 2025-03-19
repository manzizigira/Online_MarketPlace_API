package com.ecommerce.Ecommerce.Web.service;

import com.ecommerce.Ecommerce.Web.entity.Orders;
import com.ecommerce.Ecommerce.Web.entity.Products;

import java.util.List;

public interface ProductService {

    Products createProduct(Products products);

    Products getProductById(Long theId);

    List<Products> viewProducts();

    void deleteProducts(Long productId);

    List<Products> getProductByCategoryId(Long categoryId);

    void assignCategoryToProduct(Long productId, Long categoryId);

    void toggleFeatured(Long productId);

    List<Products> getFeaturedProducts();

    List<Products> getProductsByCategory(String categoryName);

    List<Products> getProductsByTag(String tag);

    List<Products> searchProducts(String query);
    void updateProduct(Products product);
}
