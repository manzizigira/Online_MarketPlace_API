package com.ecommerce.Ecommerce.Web.service;

import com.ecommerce.Ecommerce.Web.entity.Orders;
import com.ecommerce.Ecommerce.Web.entity.Products;
import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.enums.OrderStatusEnum;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Orders placeOrder(Orders orders);
    Orders trackOrder(String orderTrackingNumber);
    List<Orders> getOrdersByUserIdAndStatus(Long userId, OrderStatusEnum status);
    List<Orders> getOrdersByUserId(Long userId);
    Orders getOrderById(Long id);
    List<Orders> getOrdersByStatus(OrderStatusEnum statusEnum);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByOrderId(Long productId);

    boolean updateOrderFields(Orders orders);

    List<Orders> fetchOrders();
}
