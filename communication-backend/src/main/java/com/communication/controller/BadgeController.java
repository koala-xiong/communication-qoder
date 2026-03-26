package com.communication.controller;

import com.communication.dto.ApiResponse;
import com.communication.dto.BadgeDto;
import com.communication.service.BadgeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<BadgeDto>> getUserBadges(@PathVariable Long userId) {
        return ApiResponse.success(badgeService.getUserBadges(userId));
    }
}
