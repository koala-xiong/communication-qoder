package com.communication.dto;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @Size(max = 200, message = "简介不能超过200字")
    private String bio;

    private String avatarUrl;

    public UpdateProfileRequest() {}

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
