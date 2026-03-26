package com.communication.controller;

import com.communication.dto.ApiResponse;
import com.communication.dto.ContentDto;
import com.communication.dto.PageResponse;
import com.communication.service.TrendingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trending")
public class TrendingController {

    private final TrendingService trendingService;

    public TrendingController(TrendingService trendingService) {
        this.trendingService = trendingService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ContentDto>> getTrending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(trendingService.getTrendingContents(page, size));
    }

    @GetMapping("/weekly")
    public ApiResponse<PageResponse<ContentDto>> getWeeklyPopular(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(trendingService.getRecentPopular(page, size));
    }
}
