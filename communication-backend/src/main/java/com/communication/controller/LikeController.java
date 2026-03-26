package com.communication.controller;

import com.communication.dto.ApiResponse;
import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.service.LikeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{contentId}")
    public ApiResponse<Map<String, Object>> toggleLike(@PathVariable Long contentId, Authentication auth) {
        boolean liked = likeService.toggleLike(contentId, auth.getName());
        return ApiResponse.success(Map.of("liked", liked));
    }

    @GetMapping("/check/{contentId}")
    public ApiResponse<Boolean> checkLike(@PathVariable Long contentId, Authentication auth) {
        return ApiResponse.success(likeService.isLiked(contentId, auth.getName()));
    }

    @PostMapping("/batch-check")
    public ApiResponse<Map<Long, Boolean>> batchCheck(@RequestBody List<Long> contentIds, Authentication auth) {
        return ApiResponse.success(likeService.batchCheckLiked(contentIds, auth.getName()));
    }

    @GetMapping("/my")
    public ApiResponse<PageResponse<ContentDto>> getLikedContents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        return ApiResponse.success(likeService.getLikedContents(auth.getName(), page, size));
    }
}
