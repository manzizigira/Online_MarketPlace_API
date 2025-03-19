package com.ecommerce.Ecommerce.Web.controller;


import com.ecommerce.Ecommerce.Web.entity.Categories;
import com.ecommerce.Ecommerce.Web.entity.Orders;
import com.ecommerce.Ecommerce.Web.entity.Products;
import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.enums.OrderStatusEnum;
import com.ecommerce.Ecommerce.Web.implementation.SecurityService;
import com.ecommerce.Ecommerce.Web.service.CategoryService;
import com.ecommerce.Ecommerce.Web.service.OrderService;
import com.ecommerce.Ecommerce.Web.service.ProductService;
import com.ecommerce.Ecommerce.Web.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class ProductController {

    private ProductService productService;
    private CategoryService categoryService;
    private UserService userService;
    private OrderService orderService;
    private SecurityService securityService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, UserService userService, OrderService orderService, SecurityService securityService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.orderService = orderService;
        this.securityService = securityService;
    }


    @GetMapping("/list")
    public List<Products> getAllProducts() {
        return productService.viewProducts();
    }

    @GetMapping("/viewProductsPage")
    public String showProductsPage(Model model) {
        List<Categories> categories = categoryService.viewAllCategories();
        List<Products> products = productService.viewProducts();

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("newProduct", new Products());
        return "products";
    }

    @PostMapping("/registerProducts")
    public String createProduct(@ModelAttribute Products product, @RequestParam Long categoryId) {
        Categories category = categoryService.getCategoryById(categoryId);
        product.assignCategory(category);
        product.setActive(true);
        productService.createProduct(product);
        return "redirect:/viewProductsPage";
    }

    @PostMapping("/products/toggleFeatured")
    public String toggleFeatured(@RequestParam("id") Long productId) {
        productService.toggleFeatured(productId);
        return "redirect:/viewProductsPage";
    }


    @GetMapping("/shop")
    public String getShopPage(HttpServletRequest request, Model model) {
        // Extract the userId from the JWT or session
        Long userId = securityService.extractUserId(getJwtFromCookies(request));  // Use the appropriate method to extract the userId
        List<Categories> categories = categoryService.viewAllCategories();

        // Fetch products and add them to the model
        List<Products> products = productService.viewProducts();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("userId", userId);  // Add userId to the model

        return "shopProducts";  // Thymeleaf template name
    }

    @PostMapping("/productDetails")
    public String getProductDetails(@RequestParam Long id, Model model) {
        Products products = productService.getProductById(id);
        List<Categories> categories = categoryService.viewAllCategories();
        model.addAttribute("product", products);
        model.addAttribute("categories", categories);
        return "product-details";
    }


    @PostMapping("/prepareOrder")
    public String placeAnOrder(@RequestParam Long id, Model model) {
        Products products = productService.getProductById(id);
        List<Categories> categories = categoryService.viewAllCategories();
        model.addAttribute("product", products);
        model.addAttribute("categories", categories);
        return "placeAnOrder";
    }


    @PostMapping("/placeOrder")
    public ResponseEntity<Orders> placeOrder(@RequestParam("productId") Long productId,
                                             @RequestParam("totalQuantity") Integer totalQuantity,
                                             @CookieValue("jwtToken") String token,
                                             HttpServletResponse response) {

        try {
            // Extract user ID from the token
            Long userId = securityService.extractUserId(token);
            System.out.println("Extracted User ID: " + userId);

            // Fetch the user based on the extracted user ID
            Users user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // User not found
            }
            System.out.println("User fetched from DB: " + user);

            // Fetch product from database
            Products product = productService.getProductById(productId);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Product not found
            }
            System.out.println("Product fetched from DB: " + product);

            BigDecimal totalPrice = product.getUnitPrice().multiply(new BigDecimal(totalQuantity));

            // Create a new order
            Orders order = new Orders();
            order.setUsers(user);  // Set user on the order
            order.setStatus(OrderStatusEnum.PENDING);
            order.setOrderTrackingNumber(UUID.randomUUID().toString());
            order.setTotalQuantity(totalQuantity);
            order.setTotalPrice(totalPrice);


            // Add the product to the order
            order.getProducts().add(product);
            System.out.println("Order before saving: " + order);

            // Place the order using the OrderService
            Orders savedOrder = orderService.placeOrder(order);  // Save the order
            System.out.println("Saved Order: " + savedOrder);

            response.sendRedirect("/shop");

            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);  // Return created order


        } catch (Exception e) {
            e.printStackTrace();  // Log the exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Handle errors
        }
    }





    @GetMapping("/category/{categoryName}")
    public String getProductsByCategory(@PathVariable String categoryName, Model model) {
        List<Products> products = productService.getProductsByCategory(categoryName);
        List<Categories> categories = categoryService.viewAllCategories();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "shopProducts"; // Thymeleaf template
    }

    @GetMapping("/tag/{tag}")
    public String getProductsByTag(@PathVariable String tag, Model model) {
        List<Products> products = productService.getProductsByTag(tag);
        List<Categories> categories = categoryService.viewAllCategories();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "shopProducts"; // Same template for listing products
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam String query, Model model) {
        List<Products> products = productService.searchProducts(query);
        List<Categories> categories = categoryService.viewAllCategories();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "shopProducts";
    }


    // Helper method to get the JWT token from cookies
    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // Return null if the token is not found
    }


    @GetMapping("/viewProducts")
    public List<Products> viewListOfProducts(){
        return productService.viewProducts();
    }

    @GetMapping("/getProductById/{productId}")
    @ResponseBody
    public Products getProductById(@PathVariable Long productId) {
        return productService.getProductById(productId);
    }


    @PostMapping("/update-products")
    public String showUpdateForm(@RequestParam("id") Long productId, Model model) {
        Products product = productService.getProductById(productId);
        List<Categories> categories = categoryService.viewAllCategories();

        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categories); // Add categories to the model
            return "update-product";
        } else {
            return "redirect:/viewProductsPage";
        }
    }



    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute Products product, @RequestParam("categoryId") Long categoryId, @RequestParam("tags") String tags) {
        Categories category = categoryService.getCategoryById(categoryId);
        product.setCategories(category); // Set the category before updating
        product.setTags(tags);
        productService.updateProduct(product);
        return "redirect:/viewProductsPage";
    }

    @PostMapping("/delete-product")
    public String deleteProduct(@RequestParam("id") Long productId) {
        try {
            productService.deleteProducts(productId); // Call the delete service method
            return "redirect:/viewProductsPage"; // Redirect to the product list page
        } catch (NumberFormatException e) {
            // Handle invalid id format (though this won't happen with Long type)
            return "redirect:/viewProductsPage"; // Handle errors gracefully
        }
    }





}
