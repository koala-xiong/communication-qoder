package com.communication.dto;

import com.communication.entity.BadgeType;
import com.communication.entity.UserBadge;

import java.time.LocalDateTime;

public class BadgeDto {
    private BadgeType badgeType;
    private String displayName;
    private String description;
    private LocalDateTime earnedAt;

    public BadgeDto() {}

    public BadgeType getBadgeType() { return badgeType; }
    public void setBadgeType(BadgeType badgeType) { this.badgeType = badgeType; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getEarnedAt() { return earnedAt; }
    public void setEarnedAt(LocalDateTime earnedAt) { this.earnedAt = earnedAt; }

    public static BadgeDto fromEntity(UserBadge badge) {
        BadgeDto dto = new BadgeDto();
        dto.setBadgeType(badge.getBadgeType());
        dto.setEarnedAt(badge.getEarnedAt());
        dto.setDisplayName(getDisplayNameFor(badge.getBadgeType()));
        dto.setDescription(getDescriptionFor(badge.getBadgeType()));
        return dto;
    }

    private static String getDisplayNameFor(BadgeType type) {
        return switch (type) {
            case NEWCOMER -> "新手上路";
            case ACTIVE_WRITER -> "活跃作者";
            case POPULAR_AUTHOR -> "人气作者";
            case TOP_COMMENTER -> "评论达人";
            case LOYAL_READER -> "忠实读者";
            case EARLY_ADOPTER -> "早期用户";
        };
    }

    private static String getDescriptionFor(BadgeType type) {
        return switch (type) {
            case NEWCOMER -> "完成注册，开启创作之旅";
            case ACTIVE_WRITER -> "发布 10 篇以上内容";
            case POPULAR_AUTHOR -> "单篇内容获得 50 个以上点赞";
            case TOP_COMMENTER -> "发表 50 条以上评论";
            case LOYAL_READER -> "阅读 100 篇以上内容";
            case EARLY_ADOPTER -> "平台早期注册用户";
        };
    }
}
