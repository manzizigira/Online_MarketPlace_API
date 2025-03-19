package com.ecommerce.Ecommerce.Web.implementation;

import com.ecommerce.Ecommerce.Web.entity.Products;
import com.ecommerce.Ecommerce.Web.entity.Reviews;
import com.ecommerce.Ecommerce.Web.entity.Users;
import com.ecommerce.Ecommerce.Web.repository.ReviewsRepository;
import com.ecommerce.Ecommerce.Web.service.OrderService;
import com.ecommerce.Ecommerce.Web.service.ProductService;
import com.ecommerce.Ecommerce.Web.service.ReviewService;
import com.ecommerce.Ecommerce.Web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewImplementation implements ReviewService {

    private ReviewsRepository reviewsRepository;
    private UserService userService;
    private OrderService orderService;
    private ProductService productService;

    @Autowired
    public ReviewImplementation(ReviewsRepository reviewsRepository, UserService userService, OrderService orderService, ProductService productService) {
        this.reviewsRepository = reviewsRepository;
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @Override
    public Reviews createReview(Long userId, Long productId, int rating, String comment){
        // Check if the user exists
        Users user = userService.getUserById(userId);

        // Check if the product exists
        Products product = productService.getProductById(productId);

        // Check if the user has placed an order for this product
        boolean hasPurchased = orderService.existsByUserIdAndProductId(userId, productId);
        if (!hasPurchased) {
            throw new RuntimeException("User has not purchased this product and cannot review it");
        }

        // Create and save the review
        Reviews review = new Reviews();
        review.setUsers(user);
        review.setProducts(product);
        review.setRating(rating);
        review.setComment(comment);

        return reviewsRepository.save(review);
    }

    @Override
    public List<Reviews> getReviewsByProductId(Long productId) {
        return reviewsRepository.findByProductsId(productId);
    }

    @Override
    public List<Reviews> getReviewsByUserId(Long userId) {
        return reviewsRepository.findByUsersId(userId);
    }

    @Override
    public void deleteReviewById(Long reviewId) {
        reviewsRepository.deleteById(reviewId);
    }

    @Override
    public Reviews updateReview(Long reviewId, int rating, String comment) {
        Reviews review = reviewsRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));

        review.setRating(rating);
        review.setComment(comment);

        return reviewsRepository.save(review);
    }
}
