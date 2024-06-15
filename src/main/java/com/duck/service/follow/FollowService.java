package com.duck.service.follow;

import com.duck.dto.follow.FollowDTO;
import com.duck.dto.follow.FollowPageResponse;

import java.security.Principal;
import java.util.List;

public interface FollowService {
    FollowDTO follow(final Long productId, final Principal principal);

    FollowPageResponse getYourFollow(final int pageNo, final int pageSize, final Principal principal);

    List<Long> findProductIdsFollowedByUser(Principal principal);

    Long countYourFollow(final Principal principal);

    boolean isFollowed(final String username, final Long productId);
}
