package com.communication.dto;

import com.communication.entity.Notification;
import com.communication.entity.NotificationType;

import java.time.LocalDateTime;

public class NotificationDto {
    private Long id;
    private UserDto sender;
    private NotificationType type;
    private Long contentId;
    private String contentTitle;
    private Long commentId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public NotificationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserDto getSender() { return sender; }
    public void setSender(UserDto sender) { this.sender = sender; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
    public String getContentTitle() { return contentTitle; }
    public void setContentTitle(String contentTitle) { this.contentTitle = contentTitle; }
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static NotificationDto fromEntity(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setSender(UserDto.fromEntity(n.getSender()));
        dto.setType(n.getType());
        dto.setContentId(n.getContent() != null ? n.getContent().getId() : null);
        dto.setContentTitle(n.getContent() != null ? n.getContent().getTitle() : null);
        dto.setCommentId(n.getCommentId());
        dto.setMessage(n.getMessage());
        dto.setIsRead(n.getIsRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
