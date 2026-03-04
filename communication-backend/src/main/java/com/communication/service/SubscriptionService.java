package com.communication.service;

import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.dto.SubscriptionDto;
import com.communication.dto.UserDto;

public interface SubscriptionService {

    SubscriptionDto subscribe(Long authorId, String username);

    void unsubscribe(Long authorId, String username);

    boolean isSubscribed(Long authorId, String username);

    PageResponse<UserDto> getSubscriptions(String username, int page, int size);

    PageResponse<UserDto> getFollowers(Long userId, int page, int size);

    PageResponse<ContentDto> getSubscriptionFeed(String username, int page, int size);

    long getSubscriptionCount(Long userId);

    long getFollowerCount(Long userId);
}
