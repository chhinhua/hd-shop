package com.duck.service.review;

import com.duck.dto.review.ReviewDTO;
import com.duck.dto.review.ReviewResponse;

import java.security.Principal;

public interface ReviewService {

    ReviewDTO create(final ReviewDTO reviewDTO, Principal principal);

    ReviewResponse getProductReviews(final Long product_id, final Integer star, final int pageNo, final int pageSize);
}
