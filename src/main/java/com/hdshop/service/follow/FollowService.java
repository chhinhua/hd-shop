package com.hdshop.service.follow;

import com.hdshop.dto.follow.FollowDTO;
import com.hdshop.dto.follow.FollowPageResponse;

import java.security.Principal;
import java.util.List;

public interface FollowService {
    FollowDTO follow(final Long productId, final Principal principal);

    FollowPageResponse getYourFollow(final int pageNo, final int pageSize, final Principal principal);

    List<Long> findProductIdsFollowedByUser(Principal principal);
}
