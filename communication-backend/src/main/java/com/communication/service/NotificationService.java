package com.communication.service;

import com.communication.dto.NotificationDto;
import com.communication.dto.PageResponse;
import com.communication.entity.NotificationType;

public interface NotificationService {

    void createNotification(Long recipientId, Long senderId, NotificationType type, Long contentId, Long commentId, String message);

    PageResponse<NotificationDto> getNotifications(String username, int page, int size);

    long getUnreadCount(String username);

    void markAsRead(Long notificationId, String username);

    void markAllAsRead(String username);
}
