package com.hdshop.service.user.impl;

import com.hdshop.dto.user.FollowProductDTO;
import com.hdshop.entity.Product;
import com.hdshop.entity.User;
import com.hdshop.entity.UserFollowProduct;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.ProductRepository;
import com.hdshop.repository.UserFollowProductRepository;
import com.hdshop.repository.UserRepository;
import com.hdshop.service.user.UserFollowProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserFollowProductServiceImpl implements UserFollowProductService {
    private final UserFollowProductRepository followRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final ModelMapper modelMapper;

    @Override
    public FollowProductDTO create(Long productId, Principal principal) {
        String username = principal.getName();

        // Retrieve data
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("product-not-found")));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));

        Optional<UserFollowProduct> existingFollow = followRepository
                .findByUser_UsernameAndProduct_ProductId(username, productId);

        UserFollowProduct followProduct;
        if (existingFollow.isPresent()) {
            UserFollowProduct toggleFollow = existingFollow.get();
            toggleFollow.setDeleted(!toggleFollow.isDeleted());
            followProduct = followRepository.save(toggleFollow);
        } else {
            // Build following
            followProduct = new UserFollowProduct();
            followProduct.setProduct(product);
            followProduct.setUser(user);
            followProduct.setDeleted(false);
            followProduct = followRepository.save(followProduct);
        }

        return mapEntityToDTO(followProduct);
    }

    private FollowProductDTO mapEntityToDTO(UserFollowProduct followProduct) {
        return modelMapper.map(followProduct, FollowProductDTO.class);
    }
    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
