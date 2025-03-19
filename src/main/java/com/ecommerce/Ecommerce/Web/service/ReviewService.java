package com.ecommerce.Ecommerce.Web.service;

import com.ecommerce.Ecommerce.Web.entity.Reviews;

import java.util.List;

public interface ReviewService {

    public Reviews createReview(Long userId, Long productId, int rating, String comment);
    List<Reviews> getReviewsByProductId(Long productId);
    List<Reviews> getReviewsByUserId(Long userId);
    void deleteReviewById(Long reviewId);
    Reviews updateReview(Long reviewId, int rating, String comment);
}
