package com.hdshop.service.wishlist;

import com.hdshop.dto.wishlist.WishlistDTO;
import com.hdshop.entity.Product;
import com.hdshop.entity.User;
import com.hdshop.entity.Wishlist;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.ProductRepository;
import com.hdshop.repository.FollowRepository;
import com.hdshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final FollowRepository followRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final ModelMapper modelMapper;

    @Override
    public WishlistDTO follow(Long productId, Principal principal) {
        String username = principal.getName();

        // Retrieve data
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("product-not-found")));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));

        Optional<Wishlist> existingFollow = followRepository
                .findByUser_UsernameAndProduct_ProductId(username, productId);

        Wishlist followProduct;
        if (existingFollow.isPresent()) {
            Wishlist toggleFollow = existingFollow.get();
            toggleFollow.setDeleted(!toggleFollow.isDeleted());
            followProduct = followRepository.save(toggleFollow);
        } else {
            // Build follow
            followProduct = new Wishlist();
            followProduct.setProduct(product);
            followProduct.setUser(user);
            followProduct.setDeleted(false);
            followProduct = followRepository.save(followProduct);
        }

        return mapEntityToDTO(followProduct);
    }

    private WishlistDTO mapEntityToDTO(Wishlist followProduct) {
        return modelMapper.map(followProduct, WishlistDTO.class);
    }
    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
