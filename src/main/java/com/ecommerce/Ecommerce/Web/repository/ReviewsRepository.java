package com.ecommerce.Ecommerce.Web.repository;

import com.ecommerce.Ecommerce.Web.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewsRepository extends JpaRepository<Reviews, Long> {

    List<Reviews> findByProductsId(Long productId);
    List<Reviews> findByUsersId(Long userId);
}
