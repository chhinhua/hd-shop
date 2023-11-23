package com.hdshop.service.review;

import com.hdshop.dto.review.ReviewDTO;
import com.hdshop.entity.Order;
import com.hdshop.entity.Product;
import com.hdshop.entity.Review;
import com.hdshop.entity.User;
import com.hdshop.exception.InvalidException;
import com.hdshop.repository.ProductRepository;
import com.hdshop.repository.ReviewRepository;
import com.hdshop.service.order.OrderService;
import com.hdshop.service.product.ProductService;
import com.hdshop.service.user.UserService;
import com.hdshop.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final MessageSource messageSource;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @Override
    public ReviewDTO create(ReviewDTO dto, Principal principal) {
        // validate the review request
        validateReview(dto);

        // retrieve data
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        Order order = orderService.findById(dto.getOrderId());
        Product product = productService.findById(dto.getProductId());

        // build review
        Review review = new Review();
        review.setContent(dto.getContent() != null ? dto.getContent().trim() : "");
        review.setStars(dto.getStars());
        review.setUser(user);
        review.setProduct(product);
        review.setOrder(order);

        // save to db
        Review newReview = reviewRepository.save(review);

        // update product rating
        System.out.println("1: " + product.getNumberOfRatings());
        updateRating(product);

        return mapEntityToDTO(newReview);
    }

    private void validateReview(ReviewDTO dto) {
        if (dto.getOrderId() == null) {
            throw new InvalidException(getMessage("order-id-must-not-be-null"));
        }
        if (!AppUtils.isValidRating(dto.getStars())) {
            throw new InvalidException(getMessage("the-number-of-rating-stars-must-be-between-0-and-5"));
        }
    }

    public void updateRating(Product product) {
        List<Review> reviews = product.getReviews();
        int numberOfRatings = reviews.size();
        product.setNumberOfRatings(numberOfRatings);

        float totalStars = 0;
        for (Review review : reviews) {
            totalStars += review.getStars();
        }

        if (product.getNumberOfRatings() > 0) {
            float rating = totalStars / numberOfRatings;
            product.setRating(rating);
        }else {
            product.setRating(0f);
        }
        productRepository.save(product);
        System.out.println("2: " + numberOfRatings);
    }

    private ReviewDTO mapEntityToDTO(Review review) {
        return modelMapper.map(review, ReviewDTO.class);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
