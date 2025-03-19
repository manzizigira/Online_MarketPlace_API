package com.ecommerce.Ecommerce.Web.implementation;

import com.ecommerce.Ecommerce.Web.entity.Categories;
import com.ecommerce.Ecommerce.Web.entity.Orders;
import com.ecommerce.Ecommerce.Web.entity.Products;
import com.ecommerce.Ecommerce.Web.repository.CategoriesRepository;
import com.ecommerce.Ecommerce.Web.repository.ProductsRepository;
import com.ecommerce.Ecommerce.Web.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductImplementation implements ProductService {

    private ProductsRepository productsRepository;

    private CategoriesRepository categoriesRepository;

    @Autowired
    public ProductImplementation(ProductsRepository productsRepository, CategoriesRepository categoriesRepository) {
        this.productsRepository = productsRepository;
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public Products createProduct(Products products) {
        return productsRepository.save(products);
    }

    @Override
    public Products getProductById(Long theId) {
        Optional<Products> productId = productsRepository.findById(theId);
        Products products = null;
        if (productId != null){
            products = productId.get();
        }else{
            throw new RuntimeException("No Product of that Id Found");
        }
        return products;
    }

    @Override
    public List<Products> viewProducts() {
        return productsRepository.findAll();
    }

    @Override
    public void deleteProducts(Long productId) {
        productsRepository.deleteById(productId);
    }

    @Override
    public List<Products> getProductByCategoryId(Long categoryId) {
        return productsRepository.findByCategoriesId(categoryId);
    }

    @Override
    public void assignCategoryToProduct(Long productId, Long categoryId) {
        Optional<Products> findProductById = productsRepository.findById(productId);
        Optional<Categories> findCategoryById = categoriesRepository.findById(categoryId);

        if (findCategoryById.isPresent() && findProductById.isPresent()){
            Products product = findProductById.get();
            Categories category = findCategoryById.get();

            product.assignCategory(category);

            productsRepository.save(product);
        }
    }

    @Override
    public void toggleFeatured(Long productId) {
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Toggle featured status
        product.setFeatured(product.getFeatured() == null ? true : !product.getFeatured());

        productsRepository.save(product);
    }

    public List<Products> getFeaturedProducts() {
        return productsRepository.findByFeaturedTrue();
    }

    @Override
    public List<Products> getProductsByCategory(String categoryName) {
        return productsRepository.findByCategories_CategoryNameIgnoreCase(categoryName);
    }


    @Override
    public List<Products> getProductsByTag(String tag) {
        return productsRepository.findByTagsContainingIgnoreCase(tag);
    }

    @Override
    public List<Products> searchProducts(String query) {
        return productsRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    @Override
    public void updateProduct(Products product) {
        productsRepository.save(product);
    }
}
