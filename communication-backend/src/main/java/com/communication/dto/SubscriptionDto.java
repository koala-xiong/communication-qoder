package com.communication.dto;

import com.communication.entity.Subscription;

import java.time.LocalDateTime;

public class SubscriptionDto {

    private Long id;
    private UserDto subscriber;
    private UserDto author;
    private LocalDateTime createdAt;

    public SubscriptionDto() {}

    public SubscriptionDto(Long id, UserDto subscriber, UserDto author, LocalDateTime createdAt) {
        this.id = id;
        this.subscriber = subscriber;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserDto getSubscriber() { return subscriber; }
    public void setSubscriber(UserDto subscriber) { this.subscriber = subscriber; }
    public UserDto getAuthor() { return author; }
    public void setAuthor(UserDto author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static SubscriptionDto fromEntity(Subscription subscription) {
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .subscriber(UserDto.fromEntity(subscription.getSubscriber()))
                .author(UserDto.fromEntity(subscription.getAuthor()))
                .createdAt(subscription.getCreatedAt())
                .build();
    }

    public static SubscriptionDtoBuilder builder() { return new SubscriptionDtoBuilder(); }

    public static class SubscriptionDtoBuilder {
        private Long id;
        private UserDto subscriber;
        private UserDto author;
        private LocalDateTime createdAt;

        public SubscriptionDtoBuilder id(Long id) { this.id = id; return this; }
        public SubscriptionDtoBuilder subscriber(UserDto subscriber) { this.subscriber = subscriber; return this; }
        public SubscriptionDtoBuilder author(UserDto author) { this.author = author; return this; }
        public SubscriptionDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public SubscriptionDto build() {
            return new SubscriptionDto(id, subscriber, author, createdAt);
        }
    }
}
