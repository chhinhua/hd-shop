package com.hdshop.service.user;

import com.hdshop.dto.user.FollowProductDTO;

import java.security.Principal;

public interface UserFollowProductService {
    FollowProductDTO create(final Long productId,final Principal principal);
}
