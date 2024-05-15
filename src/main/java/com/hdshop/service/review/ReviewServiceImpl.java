package com.hdshop.service.review;

import com.hdshop.dto.review.ReviewDTO;
import com.hdshop.dto.review.ReviewResponse;
import com.hdshop.dto.review.ReviewStarNumber;
import com.hdshop.entity.*;
import com.hdshop.exception.InvalidException;
import com.hdshop.repository.ProductRepository;
import com.hdshop.repository.ReviewRepository;
import com.hdshop.service.order.OrderItemService;
import com.hdshop.service.order.OrderService;
import com.hdshop.service.product.ProductService;
import com.hdshop.service.user.UserService;
import com.hdshop.utils.AppUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements ReviewService {
    ReviewRepository reviewRepository;
    UserService userService;
    ProductService productService;
    OrderService orderService;
    OrderItemService orderItemService;
    MessageSource messageSource;
    ModelMapper modelMapper;
    ProductRepository productRepository;

    @Override
    public ReviewDTO create(ReviewDTO dto, Principal principal) {
        // validate the review request
        validateReview(dto);

        // retrieve data
        String username = principal.getName();
        User user = userService.findByUsername(username);
        Order order = orderService.findByItemId(dto.getItemId());
        OrderItem orderItem = orderItemService.findById(dto.getItemId());
        Product product = productService.findById(dto.getProductId());

        // build review
        Review review = new Review();
        review.setContent(dto.getContent() != null ? dto.getContent().trim() : "");
        review.setStars(dto.getStars());
        review.setUser(user);
        review.setProduct(product);
        review.setOrder(order);
        review.setOrderItem(orderItem);

        // save to db
        Review newReview = reviewRepository.save(review);

        // update product rating
        updateRating(product);

        return mapEntityToDTO(newReview);
    }

    @Override
    public ReviewResponse getProductReviews(Long product_id, Integer star, int pageNo, int pageSize) {
        // follow Pageable instances
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<Review> reviewPage = reviewRepository.findByProduct(product_id, star, pageable);

        // get content for page object
        List<Review> reviewList = reviewPage.getContent();

        ReviewStarNumber starNumber = buildStarNumber(product_id);
        List<ReviewDTO> content = reviewList.stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());

        // set data to the review response
        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setContent(content);
        reviewResponse.setStarNumber(starNumber);
        reviewResponse.setPageNo(reviewPage.getNumber() + 1);
        reviewResponse.setPageSize(reviewPage.getSize());
        reviewResponse.setTotalPages(reviewPage.getTotalPages());
        reviewResponse.setTotalElements(reviewPage.getTotalElements());
        reviewResponse.setLast(reviewPage.isLast());

        return reviewResponse;
    }

    private ReviewStarNumber buildStarNumber(Long product_id) {
        List<Review> reviewList = reviewRepository.findAllByProduct_ProductId(product_id);

        ReviewStarNumber starNumber = new ReviewStarNumber();
        starNumber.setAll(reviewList.size());
        starNumber.setOneStar(countByStar(reviewList, 1));
        starNumber.setTwoStar(countByStar(reviewList, 2));
        starNumber.setThreeStar(countByStar(reviewList, 3));
        starNumber.setFourStar(countByStar(reviewList, 4));
        starNumber.setFiveStar(countByStar(reviewList, 5));
        return starNumber;
    }

    private int countByStar(List<Review> reviewList, int star) {
        return (int) reviewList.stream().filter(review -> review.getStars() == star).count();
    }

    private void validateReview(ReviewDTO dto) {
        if (dto.getItemId() == null) {
            throw new InvalidException(getMessage("item-id-must-not-be-null"));
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
            float roundedRating = (float) (Math.round(rating * 10.0) / 10.0);
            product.setRating(roundedRating);
        } else {
            product.setRating(0f);
        }
        productRepository.save(product);
        System.out.println("2: " + numberOfRatings);
    }

    private ReviewDTO mapEntityToDTO(Review review) {
        ReviewDTO dto = modelMapper.map(review, ReviewDTO.class);
        dto.setSku(getSkuValue(review.getOrderItem().getSku()));
        return dto;
    }

    private String getSkuValue(ProductSku sku) {
        StringBuilder concatenatedValues = new StringBuilder();

        sku.getOptionValues().forEach(v -> {
            concatenatedValues.append(v.getValueName()).append(" - ");
        });

        // Remove the extra " - " at the end
        if (concatenatedValues.length() > 0) {
            concatenatedValues.setLength(concatenatedValues.length() - 3);
        }

        return concatenatedValues.toString();
    }


    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
