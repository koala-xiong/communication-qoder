package com.communication.dto;

import com.communication.entity.ContentStatus;
import com.communication.entity.MediaType;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateContentRequest {

    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;

    private String body;

    private String mediaUrl;

    private MediaType mediaType;

    private ContentStatus status;

    private Long categoryId;

    /** 显式清空频道（避免仅省略 categoryId 时无法区分「不修改」与「清空」） */
    private Boolean clearCategory;

    @Size(max = 10, message = "最多添加10个标签")
    private List<String> tags;

    public UpdateContentRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
    public ContentStatus getStatus() { return status; }
    public void setStatus(ContentStatus status) { this.status = status; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Boolean getClearCategory() { return clearCategory; }
    public void setClearCategory(Boolean clearCategory) { this.clearCategory = clearCategory; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
