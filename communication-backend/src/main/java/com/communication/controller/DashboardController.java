package com.communication.controller;

import com.communication.dto.*;
import com.communication.service.ContentService;
import com.communication.service.DashboardService;
import com.communication.service.FileUploadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ContentService contentService;
    private final FileUploadService fileUploadService;

    public DashboardController(DashboardService dashboardService, ContentService contentService, FileUploadService fileUploadService) {
        this.dashboardService = dashboardService;
        this.contentService = contentService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStats(Authentication authentication) {
        DashboardStatsDto stats = dashboardService.getStats(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/contents")
    public ResponseEntity<ApiResponse<PageResponse<ContentDto>>> getMyContents(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        com.communication.entity.ContentStatus contentStatus = null;
        if (status != null && !status.isEmpty()) {
            contentStatus = com.communication.entity.ContentStatus.valueOf(status.toUpperCase());
        }
        PageResponse<ContentDto> contents = contentService.getMyContents(
                authentication.getName(), contentStatus, page, size);
        return ResponseEntity.ok(ApiResponse.success(contents));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        UserDto user = dashboardService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<UserDto>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String avatarUrl = fileUploadService.uploadFile(file, "avatars");
        UserDto user = dashboardService.updateAvatar(authentication.getName(), avatarUrl);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
