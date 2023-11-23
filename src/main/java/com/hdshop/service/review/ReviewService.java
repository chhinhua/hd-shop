package com.hdshop.service.review;

import com.hdshop.dto.review.ReviewDTO;

import java.security.Principal;

public interface ReviewService {
    ReviewDTO create(final ReviewDTO reviewDTO, Principal principal);
}
