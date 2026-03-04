package com.communication.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private User subscriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Subscription() {}

    public Subscription(Long id, User subscriber, User author, LocalDateTime createdAt) {
        this.id = id;
        this.subscriber = subscriber;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getSubscriber() { return subscriber; }
    public void setSubscriber(User subscriber) { this.subscriber = subscriber; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static SubscriptionBuilder builder() { return new SubscriptionBuilder(); }

    public static class SubscriptionBuilder {
        private Long id;
        private User subscriber;
        private User author;
        private LocalDateTime createdAt;

        public SubscriptionBuilder id(Long id) { this.id = id; return this; }
        public SubscriptionBuilder subscriber(User subscriber) { this.subscriber = subscriber; return this; }
        public SubscriptionBuilder author(User author) { this.author = author; return this; }
        public SubscriptionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Subscription build() {
            return new Subscription(id, subscriber, author, createdAt);
        }
    }
}
