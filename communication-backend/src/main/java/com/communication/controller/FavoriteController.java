package com.communication.controller;

import com.communication.dto.ApiResponse;
import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.service.FavoriteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{contentId}")
    public ApiResponse<Map<String, Object>> toggleFavorite(@PathVariable Long contentId, Authentication auth) {
        boolean favorited = favoriteService.toggleFavorite(contentId, auth.getName());
        return ApiResponse.success(Map.of("favorited", favorited));
    }

    @GetMapping("/check/{contentId}")
    public ApiResponse<Boolean> checkFavorite(@PathVariable Long contentId, Authentication auth) {
        return ApiResponse.success(favoriteService.isFavorited(contentId, auth.getName()));
    }

    @PostMapping("/batch-check")
    public ApiResponse<Map<Long, Boolean>> batchCheck(@RequestBody List<Long> contentIds, Authentication auth) {
        return ApiResponse.success(favoriteService.batchCheckFavorited(contentIds, auth.getName()));
    }

    @GetMapping("/my")
    public ApiResponse<PageResponse<ContentDto>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        return ApiResponse.success(favoriteService.getFavoriteContents(auth.getName(), page, size));
    }

    @GetMapping("/count")
    public ApiResponse<Long> getCount(Authentication auth) {
        return ApiResponse.success(favoriteService.getFavoriteCount(auth.getName()));
    }
}
