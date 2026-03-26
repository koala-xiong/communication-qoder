package com.communication.controller;

import com.communication.dto.ApiResponse;
import com.communication.dto.NotificationDto;
import com.communication.dto.PageResponse;
import com.communication.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<PageResponse<NotificationDto>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        return ApiResponse.success(notificationService.getNotifications(auth.getName(), page, size));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(Authentication auth) {
        return ApiResponse.success(notificationService.getUnreadCount(auth.getName()));
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id, Authentication auth) {
        notificationService.markAsRead(id, auth.getName());
        return ApiResponse.success(null);
    }

    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(Authentication auth) {
        notificationService.markAllAsRead(auth.getName());
        return ApiResponse.success(null);
    }
}
