package com.hdshop.service.review;

import com.hdshop.dto.review.ReviewDTO;
import com.hdshop.dto.review.ReviewResponse;

import java.security.Principal;

public interface ReviewService {
    ReviewDTO create(final ReviewDTO reviewDTO, Principal principal);

    ReviewResponse getProductReviews(final Long product_id, final Integer star, final int pageNo, final int pageSize);
}
