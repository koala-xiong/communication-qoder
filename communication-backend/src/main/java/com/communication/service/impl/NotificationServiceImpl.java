package com.communication.service.impl;

import com.communication.dto.NotificationDto;
import com.communication.dto.PageResponse;
import com.communication.entity.Content;
import com.communication.entity.Notification;
import com.communication.entity.NotificationType;
import com.communication.entity.User;
import com.communication.exception.ResourceNotFoundException;
import com.communication.repository.ContentRepository;
import com.communication.repository.NotificationRepository;
import com.communication.repository.UserRepository;
import com.communication.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository, ContentRepository contentRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
    }

    @Override
    @Transactional
    public void createNotification(Long recipientId, Long senderId, NotificationType type,
                                   Long contentId, Long commentId, String message) {
        if (recipientId.equals(senderId)) return;

        User recipient = userRepository.findById(recipientId).orElse(null);
        User sender = userRepository.findById(senderId).orElse(null);
        if (recipient == null || sender == null) return;

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setType(type);
        notification.setMessage(message);
        notification.setCommentId(commentId);

        if (contentId != null) {
            Content content = contentRepository.findById(contentId).orElse(null);
            notification.setContent(content);
        }

        notificationRepository.save(notification);
    }

    @Override
    public PageResponse<NotificationDto> getNotifications(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<Notification> notifications = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size));
        List<NotificationDto> dtos = notifications.getContent().stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
        return new PageResponse<>(dtos, notifications.getNumber(), notifications.getSize(),
                notifications.getTotalElements(), notifications.getTotalPages(),
                notifications.isFirst(), notifications.isLast());
    }

    @Override
    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.countByRecipientIdAndIsReadFalse(user.getId());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        notificationRepository.markAsRead(notificationId, user.getId());
    }

    @Override
    @Transactional
    public void markAllAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        notificationRepository.markAllAsRead(user.getId());
    }
}
