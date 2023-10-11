package com.hdshop.dtos.product;

import com.hdshop.entities.CartItem;
import com.hdshop.entities.OrderItem;
import com.hdshop.entities.Review;
import com.hdshop.entities.UserFollowProduct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    @NotBlank(message = "Product name must not be empty")
    private String name;

    @NotBlank(message = "Product description must not be empty")
    @Size(max = 1000, message = "Product description must not exceed 1000 characters")
    private String description;

    private String slug;

    private BigDecimal price;

    private BigDecimal promotionalPrice;

    private int quantity = 0;

    private int quantityAvailable = 0;

    private int sold = 0;

    private float rating;

    private int numberOfRatings = 0;

    private int favoriteCount = 0;

    private Boolean isActive = true;

    private Boolean isSelling = true;

    private String createdBy;

    private String lastModifiedBy;

    private List<String> listImages = new ArrayList<String>();

    private Long categoryId;

    private List<Review> reviews = new ArrayList<>();

    private List<OrderItem> orderItems = new ArrayList<>();

    private List<CartItem> cartItems = new ArrayList<>();

    private List<UserFollowProduct> userFollowProducts = new ArrayList<>();
}
