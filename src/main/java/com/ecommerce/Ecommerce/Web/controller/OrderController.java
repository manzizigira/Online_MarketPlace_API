package com.ecommerce.Ecommerce.Web.controller;

import com.ecommerce.Ecommerce.Web.entity.Categories;
import com.ecommerce.Ecommerce.Web.entity.Orders;
import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.enums.OrderStatusEnum;
import com.ecommerce.Ecommerce.Web.enums.RoleEnum;
import com.ecommerce.Ecommerce.Web.implementation.EmailService;
import com.ecommerce.Ecommerce.Web.implementation.SecurityService;
import com.ecommerce.Ecommerce.Web.service.CategoryService;
import com.ecommerce.Ecommerce.Web.service.OrderService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class OrderController {

    private OrderService orderService;
    private SecurityService securityService;
    private EmailService emailService;
    private CategoryService categoryService;

    @Autowired
    public OrderController(OrderService orderService, SecurityService securityService, EmailService emailService, CategoryService categoryService) {
        this.orderService = orderService;
        this.securityService = securityService;
        this.emailService = emailService;
        this.categoryService = categoryService;
    }

    @GetMapping("/user/orders")
    public String getUserOrders(HttpServletRequest request, Model model) {
        // Extract the JWT token from cookies
        String token = getJwtFromCookies(request);

        if (token == null) {
            return "redirect:/login";  // Redirect to login page if no token is found
        }

        try {
            // Extract the userId from the token
            Long userId = securityService.extractUserId(token);  // This will fetch the userId from the token

            // Fetch orders with status PENDING for the current user
            List<Orders> pendingOrders = orderService.getOrdersByUserIdAndStatus(userId, OrderStatusEnum.PENDING);

            List<Categories> categories = categoryService.viewAllCategories();

            // Add the pending orders to the model
            model.addAttribute("orders", pendingOrders);
            model.addAttribute("categories", categories);

            return "shopper/pendingOrders";  // Thymeleaf template name (pendingOrders.html)
        } catch (Exception e) {
            return "error";  // Handle the exception
        }
    }

    @GetMapping("/user/order-history")
    public String getOrderHistory(HttpServletRequest request, Model model) {
        // Extract the JWT token from cookies
        String token = getJwtFromCookies(request);

        if (token == null) {
            return "redirect:/login";  // Redirect to login page if no token is found
        }

        try {
            // Extract the userId from the token
            Long userId = securityService.extractUserId(token);  // This will fetch the userId from the token

            // Fetch all orders for the current user (regardless of their status)
            List<Orders> allOrders = orderService.getOrdersByUserId(userId);

            List<Categories> categories = categoryService.viewAllCategories();

            // Add the orders to the model
            model.addAttribute("orders", allOrders);
            model.addAttribute("categories", categories);

            return "shopper/order-history";  // Thymeleaf template name (order-history.html)
        } catch (Exception e) {
            return "error";  // Handle the exception
        }
    }


    @PostMapping("/order-details")
    public String getOrderDetails(@RequestParam Long id, Model model) {
        Orders order = orderService.getOrderById(id);

        List<Categories> categories = categoryService.viewAllCategories();
        model.addAttribute("order", order);
        model.addAttribute("categories", categories);
        return "shopper/order-details";
    }


    @Scheduled(cron = "0 0 8 * * ?") // Runs every day at 8 AM
    public void sendDailyOrderStatusNotifications() {
        List<Orders> orders = orderService.getOrdersByStatus(OrderStatusEnum.PENDING);

        // Track users who have already been sent an email today
        Set<String> emailedUsers = new HashSet<>();

        for (Orders order : orders) {
            String userEmail = order.getUsers().getEmail();

            // Skip sending an email if the user has already been emailed today
            if (emailedUsers.contains(userEmail)) {
                continue;
            }

            // Mark the user as emailed
            emailedUsers.add(userEmail);

            String subject = "Order Status Update";
            String text = "Dear " + order.getUsers().getFirstName() + ",\n\n" +
                    "Your order with tracking number " + order.getOrderTrackingNumber() + " is still pending. We will update you once it's processed.\n\nThank you for your patience!";

            // Send the email
            emailService.sendEmail(userEmail, subject, text);
        }
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

    @GetMapping("/viewOrderManagement")
    public String showUserManagement(Model model){
        List<Orders> ordersList = orderService.fetchOrders();
        model.addAttribute("orders", ordersList);

        return "admin/ordersManagement";

    }


    @PostMapping("/update-order")
    public String updateUser(@ModelAttribute Orders orders, Model model) {
        // Call service to update only the required fields of the user
        boolean updated = orderService.updateOrderFields(orders);

        if (updated) {
            model.addAttribute("message", "Order updated successfully");
        } else {
            model.addAttribute("message", "Failed to update order");
        }

        return "redirect:/viewOrderManagement";  // Redirect to user details page
    }

    @PostMapping("/update-orders")
    public String showUpdateForm(@RequestParam("id") Long productId, Model model) {
        Orders orders = orderService.getOrderById(productId);

        if (orders != null) {
            model.addAttribute("order", orders);
            return "admin/order-update";
        } else {
            return "redirect:/viewOrderManagement";
        }
    }

    @PostMapping("/delete-order")
    public String deleteProduct(@RequestParam("id") Long productId) {
        try {
            orderService.deleteByOrderId(productId); // Call the delete service method
            return "redirect:/viewOrderManagement"; // Redirect to the product list page
        } catch (NumberFormatException e) {
            // Handle invalid id format (though this won't happen with Long type)
            return "error"; // Handle errors gracefully
        }
    }

}
