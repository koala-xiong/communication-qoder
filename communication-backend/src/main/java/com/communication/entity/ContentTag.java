package com.communication.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "content_tags")
public class ContentTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(nullable = false, length = 50)
    private String tag;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public ContentTag() {}

    public ContentTag(Long id, Content content, String tag, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.tag = tag;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ContentTagBuilder builder() { return new ContentTagBuilder(); }

    public static class ContentTagBuilder {
        private Long id;
        private Content content;
        private String tag;
        private LocalDateTime createdAt;

        public ContentTagBuilder id(Long id) { this.id = id; return this; }
        public ContentTagBuilder content(Content content) { this.content = content; return this; }
        public ContentTagBuilder tag(String tag) { this.tag = tag; return this; }
        public ContentTagBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ContentTag build() {
            return new ContentTag(id, content, tag, createdAt);
        }
    }
}
