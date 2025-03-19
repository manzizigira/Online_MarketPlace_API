package com.ecommerce.Ecommerce.Web.repository;

import com.ecommerce.Ecommerce.Web.entity.Orders;
import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Page<Orders> findByUsersEmailOrderByDateCreatedDesc(@Param("email") String email, Pageable pageable);
    Optional<Orders> findByOrderTrackingNumber(String orderTrackingNumber);
    List<Users> findByUsers(Users users);

    public List<Orders> findByUsersIdAndStatus(Long userId, OrderStatusEnum status);

    public List<Orders> findOrdersByStatus(OrderStatusEnum statusEnum);

    public List<Orders> findByUsersId(Long userId);
    boolean existsByUsersIdAndProductsId(Long userId, Long productId);

}
