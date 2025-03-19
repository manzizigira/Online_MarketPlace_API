package com.ecommerce.Ecommerce.Web.controller;

import com.ecommerce.Ecommerce.Web.entity.Reviews;
import com.ecommerce.Ecommerce.Web.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Endpoint to submit a new review
    @PostMapping("/submit")
    public Reviews submitReview(@RequestParam Long userId, @RequestParam Long productId,
                                @RequestParam int rating, @RequestParam String comment) {
        return reviewService.createReview(userId, productId, rating, comment);
    }

    // Endpoint to update an existing review
    @PutMapping("/update/{reviewId}")
    public Reviews updateReview(@PathVariable Long reviewId, @RequestParam int rating, @RequestParam String comment) {
        return reviewService.updateReview(reviewId, rating, comment);
    }

    // Endpoint to view all reviews for a specific product
    @GetMapping("/product/{productId}")
    public List<Reviews> getReviewsByProduct(@PathVariable Long productId) {
        return reviewService.getReviewsByProductId(productId);
    }
}
