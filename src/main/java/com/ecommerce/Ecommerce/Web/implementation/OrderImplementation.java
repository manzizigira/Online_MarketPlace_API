package com.ecommerce.Ecommerce.Web.implementation;

import com.ecommerce.Ecommerce.Web.enums.OrderStatusEnum;
import com.ecommerce.Ecommerce.Web.entity.Orders;
import com.ecommerce.Ecommerce.Web.entity.Products;
import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.repository.OrdersRepository;
import com.ecommerce.Ecommerce.Web.repository.ProductsRepository;
import com.ecommerce.Ecommerce.Web.repository.UsersRepository;
import com.ecommerce.Ecommerce.Web.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderImplementation implements OrderService {

    private OrdersRepository ordersRepository;

    private UsersRepository usersRepository;

    private ProductsRepository productsRepository;
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderImplementation(OrdersRepository ordersRepository, UsersRepository usersRepository, ProductsRepository productsRepository, RabbitTemplate rabbitTemplate) {
        this.ordersRepository = ordersRepository;
        this.usersRepository = usersRepository;
        this.productsRepository = productsRepository;
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public Orders placeOrder(Orders orders) {
        return ordersRepository.save(orders);
    }


    @RabbitListener(queues = "orderQueue")
    public void processOrder(Orders order) {
        try {
            // Process order (e.g., update stock, change status, etc.)
            order.setStatus(OrderStatusEnum.PROCESSING); // Update order status
            ordersRepository.save(order); // Update order in DB

            // Process the order: send notifications, generate invoices, etc.
            // You can also add more business logic here
            System.out.println("Order " + order.getOrderTrackingNumber() + " processed.");

        } catch (Exception e) {
            // Handle errors (logging, retries, etc.)
            System.err.println("Error processing order: " + e.getMessage());
            // Optionally, you could send the message back to the queue if processing fails
        }
    }

    @Override
    public Orders trackOrder(String orderTrackingNumber) {
        return ordersRepository.findByOrderTrackingNumber(orderTrackingNumber).orElseThrow(
                () -> new RuntimeException("Order not found")
        );
    }

    @Override
    public List<Orders> getOrdersByUserIdAndStatus(Long userId, OrderStatusEnum status) {
        return ordersRepository.findByUsersIdAndStatus(userId, status);
    }

    @Override
    public List<Orders> getOrdersByUserId(Long userId) {
        return ordersRepository.findByUsersId(userId);
    }

    @Override
    public Orders getOrderById(Long id) {
        return ordersRepository.findById(id).orElse(null);
    }

    @Override
    public List<Orders> getOrdersByStatus(OrderStatusEnum statusEnum) {
        return ordersRepository.findOrdersByStatus(statusEnum);
    }

    @Override
    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return ordersRepository.existsByUsersIdAndProductsId(userId, productId);
    }

    @Override
    public void deleteByOrderId(Long productId) {
        ordersRepository.deleteById(productId);
    }

    @Override
    public boolean updateOrderFields(Orders orders) {
        Optional<Orders> existingOrderOpt = ordersRepository.findById(orders.getId());

        if (existingOrderOpt.isPresent()) {
            Orders existingOrder = existingOrderOpt.get();

            // Update only specific fields

            // Check if totalQuantity is not null and not zero
            if (orders.getTotalQuantity() != 0) {
                existingOrder.setTotalQuantity(orders.getTotalQuantity());
            }

            // Check if totalPrice is not null and not zero
            if (orders.getTotalPrice() != null && orders.getTotalPrice().compareTo(BigDecimal.ZERO) != 0) {
                existingOrder.setTotalPrice(orders.getTotalPrice());
            }


            // Save the updated user back to the database
            ordersRepository.save(existingOrder);
            return true;
        }
        return false;
    }

    @Override
    public List<Orders> fetchOrders() {
        return ordersRepository.findAll();
    }
}
