package com.ecommerce.Ecommerce.Web.controller;

import com.ecommerce.Ecommerce.Web.entity.Categories;
import com.ecommerce.Ecommerce.Web.entity.Products;
import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.enums.RoleEnum;
import com.ecommerce.Ecommerce.Web.implementation.SecurityService;
import com.ecommerce.Ecommerce.Web.implementation.UserPrincipal;
import com.ecommerce.Ecommerce.Web.service.CategoryService;
import com.ecommerce.Ecommerce.Web.service.JWTService;
import com.ecommerce.Ecommerce.Web.service.ProductService;
import com.ecommerce.Ecommerce.Web.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@SessionAttributes("userName")
public class UserController {

    private UserService userService;

    private JWTService jwtService;

    private SecurityService securityService;

    private CategoryService categoryService;
    private ProductService productService;


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, JWTService jwtService, SecurityService securityService, CategoryService categoryService, ProductService productService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.securityService = securityService;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/viewRegistrationPage")
    public String getP(){
        return "userRegistration";
    }

    @GetMapping("/viewLoginPage")
    public String showLoginPage(HttpServletResponse response) {
        // Remove the JWT token when accessing the login page
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);  // Prevent JavaScript access to the cookie
        cookie.setSecure(true);  // Only send cookie over HTTPS (should be enabled in production)
        cookie.setPath("/");  // Path where the cookie is available
        cookie.setMaxAge(0);  // Expire the cookie immediately
        response.addCookie(cookie);
        return "userLogin";
    }

    @GetMapping("/viewAdminPage")
    public String showAdminPage(){
        return "admin/adminPage";
    }

    @GetMapping("/viewShopperPage")
    public String showShopperPage(Model model){
        List<Categories> categories = categoryService.viewAllCategories();

        // Fetch products and add them to the model
        List<Products> products = productService.viewProducts();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "shopper/shopProducts";
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Users users) {
        Map<String, String> response = new HashMap<>();

        if (userService.existsByEmail(users.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Email already exists"));
        }

        if (userService.existsByUsername(users.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Username already exists"));
        }

        try {
            users.setRoles(RoleEnum.SHOPPER);
            userService.registerUser(users); // Register the user without saving the result to a variable
            response.put("message", "Registration successful! Please check your email for verification.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 status for success
        } catch (Exception e) {
            response.put("message", "Registration failed! " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400 status for error
        }
    }

    @GetMapping("/manage-profile")
    public String manageProfile(Model model, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        // Fetch user data from the service layer using the logged-in user's ID
        String username = userPrincipal.getUsername();
        Users user = userService.getUsername(username);

        List<Categories> categories = categoryService.viewAllCategories();

        // Fetch products and add them to the model
        List<Products> products = productService.viewProducts();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        // Add the user object to the model
        model.addAttribute("user", user);

        // Return the view name to the Thymeleaf template
        return "shopper/userProfile";
    }

    @GetMapping("/viewUserManagement")
    public String showUserManagement(Model model){
        List<Users> usersList = userService.fetchUsers();
        model.addAttribute("users", usersList);
        model.addAttribute("newUser", new Users());

        return "admin/userManagement";

    }

    // Update method to handle user update
    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute Users user, Model model) {
        // Call service to update only the required fields of the user
        boolean updated = userService.updateUserFields(user);

        if (updated) {
            model.addAttribute("message", "User updated successfully");
        } else {
            model.addAttribute("message", "Failed to update user");
        }

        return "redirect:/viewUserManagement";  // Redirect to user details page
    }

    @PostMapping("/updateUser")
    public String updateUserProfile(@ModelAttribute Users user, Model model) {
        // Call service to update only the required fields of the user
        boolean updated = userService.updateUserFields(user);

        if (updated) {
            model.addAttribute("message", "User updated successfully");
        } else {
            model.addAttribute("message", "Failed to update user");
        }

        return "redirect:/viewShopperPage";  // Redirect to user details page
    }

    @PostMapping("/update-users")
    public String showUpdateForm(@RequestParam("id") Long productId, Model model) {
        Users users = userService.getUserById(productId);

        if (users != null) {
            model.addAttribute("user", users);
            return "update-user";
        } else {
            return "redirect:/viewUserManagement";
        }
    }

    @PostMapping("/delete-user")
    public String deleteProduct(@RequestParam("id") Long productId) {
        try {
            userService.deleteUserId(productId); // Call the delete service method
            return "redirect:/viewUserManagement"; // Redirect to the product list page
        } catch (NumberFormatException e) {
            // Handle invalid id format (though this won't happen with Long type)
            return "redirect:/viewUserManagement"; // Handle errors gracefully
        }
    }



    @PostMapping("/login")
    public void loginUser(@RequestBody Users users, HttpServletResponse response, Model model) throws IOException {

        String token = userService.verifyUser(users);

        if ("Fail".equals(token)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication Failed");
            return;
        }


        // Fetch the user object after successful authentication to check the verification status
        Users authenticatedUser = userService.getUsername(users.getUsername());

        model.addAttribute("userName", authenticatedUser.getUsername());

        // Check if the user's account is verified
        if (!authenticatedUser.isVerified()) {
            // Redirect to verification reminder page if not verified
            response.setHeader("Location", "/verificationReminder");
            return;
        }

        // If the user is verified, regenerate the JWT token (optional, based on your requirement)
        String newToken = jwtService.generateToken(authenticatedUser, authenticatedUser.getJwtSecretKey());


        // Set the JWT token in a cookie
        Cookie cookie = new Cookie("jwtToken", newToken);
        cookie.setHttpOnly(true);  // Prevent JavaScript access to the cookie
        cookie.setSecure(true);  // Only send cookie over HTTPS (should be enabled in production)
        cookie.setPath("/");  // Path where the cookie is available
        cookie.setMaxAge(60 * 60);  // Set cookie expiration time (e.g., 1 hour)
        response.addCookie(cookie);


        // Check user's role using the roles stored in the token
        String roles = securityService.extractRoles(newToken);
        System.out.println("User roles from token: " + roles);

        // Redirect based on role from token
        if (roles.contains("ADMIN")) {
            response.setHeader("Location", "/viewAdminPage");
        } else if (roles.contains("SHOPPER")) {
            response.setHeader("Location", "/viewShopperPage");
        } else {
            response.setHeader("Location", "/viewLoginPage");  // Default redirect
        }
    }

    @PostMapping("/logout")
    public void logoutUser(HttpServletResponse response) throws IOException {
        // Remove the JWT token by setting its expiration date in the past
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);  // Prevent JavaScript access to the cookie
        cookie.setSecure(true);  // Only send cookie over HTTPS (should be enabled in production)
        cookie.setPath("/");  // Path where the cookie is available
        cookie.setMaxAge(0);  // Expire the cookie immediately
        response.addCookie(cookie);

        // Redirect to the login page
        response.sendRedirect("/viewLoginPage");
        response.setStatus(HttpServletResponse.SC_FOUND);  // 302 redirect
    }





    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token){
        Users user = userService.getUserByEmailVerificationToken(token);

        if (user == null){
            return "Invalid Token";
        }

        user.setVerified(true);
        user.setEmailVerificationToken(null);
        userService.updateUser(user);

        return "email-verification";
    }

    @GetMapping("/verificationReminder")
    public String verificationPage(Model model){
        String username = (String) model.getAttribute("userName");
        model.addAttribute("username", username);
        return "verification-reminder";
    }

    @PostMapping("/resendVerification")
    public String resendVerificationPage(@RequestParam("username") String username,HttpServletResponse response){
        // Look up the user by their username
        Users user = userService.getUsername(username);

        // If the user is not found, you can handle the error
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "User not Found!"; // A page indicating user not found
        }

        // Check if the user is already verified
        if (user.isVerified()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "User Already Exists!"; // A page saying user is already verified
        }

        // Trigger the verification email
        userService.sendVerificationEmail(user);

        // Redirect to a confirmation page or show a success message
        return "redirect:/viewLoginPage"; // A page indicating that the verification email was sent

    }




}
