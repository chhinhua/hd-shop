package com.hdshop.service.follow;

import com.hdshop.dto.follow.FollowDTO;
import com.hdshop.dto.follow.FollowPageResponse;
import com.hdshop.entity.Follow;
import com.hdshop.entity.Product;
import com.hdshop.entity.User;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.FollowRepository;
import com.hdshop.repository.ProductRepository;
import com.hdshop.repository.UserRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowServiceImpl implements FollowService {
    FollowRepository followRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    MessageSource messageSource;
    ModelMapper modelMapper;

    @Override
    public FollowDTO follow(Long productId, Principal principal) {
        String username = principal.getName();

        // Retrieve data
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("product-not-found")));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));

        Optional<Follow> existingFollow = followRepository
                .findByUser_UsernameAndProduct_ProductId(username, productId);

        Follow followProduct;
        if (existingFollow.isPresent()) {
            Follow changeDeleted = existingFollow.get();
            changeDeleted.setIsDeleted(!changeDeleted.getIsDeleted());
            followProduct = followRepository.save(changeDeleted);

            if (changeDeleted.getIsDeleted()) {
                product.setFavoriteCount(product.getFavoriteCount() - 1);
            } else {
                product.setFavoriteCount(product.getFavoriteCount() + 1);
            }
        } else {
            // Build follow
            followProduct = new Follow();
            followProduct.setProduct(product);
            followProduct.setUser(user);
            followProduct.setIsDeleted(false);
            followProduct = followRepository.save(followProduct);

            product.setFavoriteCount(product.getFavoriteCount() + 1);
        }

        // save favoriteCount
        productRepository.save(product);

        return mapEntityToDTO(followProduct);
    }

    @Override
    public FollowPageResponse getYourFollow(final int pageNo, final int pageSize, Principal principal) {
        String username = principal.getName();

        // follow Pageable instances
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<Follow> followPage = followRepository
                .findAllByUser_UsernameAndIsDeletedIsFalseOrderByLastModifiedDateDesc(username, pageable);

        // get content for page object
        List<Follow> followListList = followPage.getContent();

        List<FollowDTO> content = followListList.stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());

        // set data to the follow response
        FollowPageResponse followResponse = new FollowPageResponse();
        followResponse.setContent(content);
        followResponse.setPageNo(followPage.getNumber() + 1);
        followResponse.setPageSize(followPage.getSize());
        followResponse.setTotalPages(followPage.getTotalPages());
        followResponse.setTotalElements(followPage.getTotalElements());
        followResponse.setLast(followPage.isLast());

        return followResponse;
    }

    @Override
    public List<Long> findProductIdsFollowedByUser(Principal principal) {
        String username = principal.getName();
        return followRepository.findProductIdsFollowedByUser(username);
    }

    @Override
    public Long countYourFollow(Principal principal) {
        if (principal == null) {
            return 0L;
        }
        String username = principal.getName();
        return followRepository.countYourFollow(username);
    }

    @Override
    public boolean isFollowed(String username, Long productId) {
        return followRepository.existsByProduct_ProductIdAndUser_UsernameAndIsDeletedFalse(
                productId,
                username
        );
    }

    private FollowDTO mapEntityToDTO(Follow follow) {
        FollowDTO dto = modelMapper.map(follow, FollowDTO.class);
        dto.getProduct().setImageUrl(
                follow.getProduct().getListImages().get(0)
        );
        return dto;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
