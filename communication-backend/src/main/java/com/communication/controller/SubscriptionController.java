package com.communication.controller;

import com.communication.dto.*;
import com.communication.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/{authorId}")
    public ResponseEntity<ApiResponse<SubscriptionDto>> subscribe(
            @PathVariable Long authorId,
            Authentication authentication) {
        SubscriptionDto subscription = subscriptionService.subscribe(authorId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }

    @DeleteMapping("/{authorId}")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(
            @PathVariable Long authorId,
            Authentication authentication) {
        subscriptionService.unsubscribe(authorId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/check/{authorId}")
    public ResponseEntity<ApiResponse<Boolean>> checkSubscription(
            @PathVariable Long authorId,
            Authentication authentication) {
        boolean isSubscribed = subscriptionService.isSubscribed(authorId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(isSubscribed));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getMySubscriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        PageResponse<UserDto> subscriptions = subscriptionService.getSubscriptions(authentication.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<UserDto> followers = subscriptionService.getFollowers(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(followers));
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<PageResponse<ContentDto>>> getSubscriptionFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<ContentDto> feed = subscriptionService.getSubscriptionFeed(authentication.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success(feed));
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<ApiResponse<SubscriptionCountDto>> getSubscriptionCount(@PathVariable Long userId) {
        long subscriptions = subscriptionService.getSubscriptionCount(userId);
        long followers = subscriptionService.getFollowerCount(userId);
        return ResponseEntity.ok(ApiResponse.success(new SubscriptionCountDto(subscriptions, followers)));
    }
}
