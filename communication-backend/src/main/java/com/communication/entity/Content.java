package com.communication.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType = MediaType.TEXT;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status = ContentStatus.PUBLISHED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentTag> tags = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Content() {}

    public Content(Long id, User author, String title, String body, String mediaUrl, MediaType mediaType, 
                   Integer viewCount, Integer commentCount, Integer likeCount, ContentStatus status,
                   Category category, List<ContentTag> tags,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.body = body;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType != null ? mediaType : MediaType.TEXT;
        this.viewCount = viewCount != null ? viewCount : 0;
        this.commentCount = commentCount != null ? commentCount : 0;
        this.likeCount = likeCount != null ? likeCount : 0;
        this.status = status != null ? status : ContentStatus.PUBLISHED;
        this.category = category;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public ContentStatus getStatus() { return status; }
    public void setStatus(ContentStatus status) { this.status = status; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<ContentTag> getTags() { return tags; }
    public void setTags(List<ContentTag> tags) { this.tags = tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static ContentBuilder builder() { return new ContentBuilder(); }

    public static class ContentBuilder {
        private Long id;
        private User author;
        private String title;
        private String body;
        private String mediaUrl;
        private MediaType mediaType = MediaType.TEXT;
        private Integer viewCount = 0;
        private Integer commentCount = 0;
        private Integer likeCount = 0;
        private ContentStatus status = ContentStatus.PUBLISHED;
        private Category category;
        private List<ContentTag> tags = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ContentBuilder id(Long id) { this.id = id; return this; }
        public ContentBuilder author(User author) { this.author = author; return this; }
        public ContentBuilder title(String title) { this.title = title; return this; }
        public ContentBuilder body(String body) { this.body = body; return this; }
        public ContentBuilder mediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; return this; }
        public ContentBuilder mediaType(MediaType mediaType) { this.mediaType = mediaType; return this; }
        public ContentBuilder viewCount(Integer viewCount) { this.viewCount = viewCount; return this; }
        public ContentBuilder commentCount(Integer commentCount) { this.commentCount = commentCount; return this; }
        public ContentBuilder likeCount(Integer likeCount) { this.likeCount = likeCount; return this; }
        public ContentBuilder status(ContentStatus status) { this.status = status; return this; }
        public ContentBuilder category(Category category) { this.category = category; return this; }
        public ContentBuilder tags(List<ContentTag> tags) { this.tags = tags; return this; }
        public ContentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ContentBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Content build() {
            return new Content(id, author, title, body, mediaUrl, mediaType, viewCount, commentCount, likeCount, status, category, tags, createdAt, updatedAt);
        }
    }
}
