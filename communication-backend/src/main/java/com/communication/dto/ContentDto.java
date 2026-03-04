package com.communication.dto;

import com.communication.entity.Content;
import com.communication.entity.ContentStatus;
import com.communication.entity.MediaType;

import java.time.LocalDateTime;
import java.util.List;

public class ContentDto {
    private Long id;
    private String title;
    private String body;
    private String mediaUrl;
    private MediaType mediaType;
    private Integer viewCount;
    private Integer commentCount;
    private ContentStatus status;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDto author;

    public ContentDto() {}

    public ContentDto(Long id, String title, String body, String mediaUrl, MediaType mediaType,
                      Integer viewCount, Integer commentCount, ContentStatus status, List<String> tags,
                      LocalDateTime createdAt, LocalDateTime updatedAt, UserDto author) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.status = status;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.author = author;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public ContentStatus getStatus() { return status; }
    public void setStatus(ContentStatus status) { this.status = status; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public UserDto getAuthor() { return author; }
    public void setAuthor(UserDto author) { this.author = author; }

    public static ContentDto fromEntity(Content content) {
        return ContentDto.builder()
                .id(content.getId())
                .title(content.getTitle())
                .body(content.getBody())
                .mediaUrl(content.getMediaUrl())
                .mediaType(content.getMediaType())
                .viewCount(content.getViewCount())
                .commentCount(content.getCommentCount())
                .status(content.getStatus())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .author(UserDto.fromEntity(content.getAuthor()))
                .build();
    }

    public static ContentDtoBuilder builder() { return new ContentDtoBuilder(); }

    public static class ContentDtoBuilder {
        private Long id;
        private String title;
        private String body;
        private String mediaUrl;
        private MediaType mediaType;
        private Integer viewCount;
        private Integer commentCount;
        private ContentStatus status;
        private List<String> tags;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UserDto author;

        public ContentDtoBuilder id(Long id) { this.id = id; return this; }
        public ContentDtoBuilder title(String title) { this.title = title; return this; }
        public ContentDtoBuilder body(String body) { this.body = body; return this; }
        public ContentDtoBuilder mediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; return this; }
        public ContentDtoBuilder mediaType(MediaType mediaType) { this.mediaType = mediaType; return this; }
        public ContentDtoBuilder viewCount(Integer viewCount) { this.viewCount = viewCount; return this; }
        public ContentDtoBuilder commentCount(Integer commentCount) { this.commentCount = commentCount; return this; }
        public ContentDtoBuilder status(ContentStatus status) { this.status = status; return this; }
        public ContentDtoBuilder tags(List<String> tags) { this.tags = tags; return this; }
        public ContentDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ContentDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public ContentDtoBuilder author(UserDto author) { this.author = author; return this; }

        public ContentDto build() {
            return new ContentDto(id, title, body, mediaUrl, mediaType, viewCount, commentCount, status, tags, createdAt, updatedAt, author);
        }
    }
}
