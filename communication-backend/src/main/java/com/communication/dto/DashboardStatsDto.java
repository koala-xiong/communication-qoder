package com.communication.dto;

public class DashboardStatsDto {
    private long totalContents;
    private long publishedContents;
    private long draftContents;
    private long totalViews;
    private long totalComments;
    private long followerCount;
    private long followingCount;

    public DashboardStatsDto() {}

    public DashboardStatsDto(long totalContents, long publishedContents, long draftContents, long totalViews,
                             long totalComments, long followerCount, long followingCount) {
        this.totalContents = totalContents;
        this.publishedContents = publishedContents;
        this.draftContents = draftContents;
        this.totalViews = totalViews;
        this.totalComments = totalComments;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }

    public long getTotalContents() { return totalContents; }
    public void setTotalContents(long totalContents) { this.totalContents = totalContents; }
    public long getPublishedContents() { return publishedContents; }
    public void setPublishedContents(long publishedContents) { this.publishedContents = publishedContents; }
    public long getDraftContents() { return draftContents; }
    public void setDraftContents(long draftContents) { this.draftContents = draftContents; }
    public long getTotalViews() { return totalViews; }
    public void setTotalViews(long totalViews) { this.totalViews = totalViews; }
    public long getTotalComments() { return totalComments; }
    public void setTotalComments(long totalComments) { this.totalComments = totalComments; }
    public long getFollowerCount() { return followerCount; }
    public void setFollowerCount(long followerCount) { this.followerCount = followerCount; }
    public long getFollowingCount() { return followingCount; }
    public void setFollowingCount(long followingCount) { this.followingCount = followingCount; }

    public static DashboardStatsDtoBuilder builder() { return new DashboardStatsDtoBuilder(); }

    public static class DashboardStatsDtoBuilder {
        private long totalContents;
        private long publishedContents;
        private long draftContents;
        private long totalViews;
        private long totalComments;
        private long followerCount;
        private long followingCount;

        public DashboardStatsDtoBuilder totalContents(long totalContents) { this.totalContents = totalContents; return this; }
        public DashboardStatsDtoBuilder publishedContents(long publishedContents) { this.publishedContents = publishedContents; return this; }
        public DashboardStatsDtoBuilder draftContents(long draftContents) { this.draftContents = draftContents; return this; }
        public DashboardStatsDtoBuilder totalViews(long totalViews) { this.totalViews = totalViews; return this; }
        public DashboardStatsDtoBuilder totalComments(long totalComments) { this.totalComments = totalComments; return this; }
        public DashboardStatsDtoBuilder followerCount(long followerCount) { this.followerCount = followerCount; return this; }
        public DashboardStatsDtoBuilder followingCount(long followingCount) { this.followingCount = followingCount; return this; }

        public DashboardStatsDto build() {
            return new DashboardStatsDto(totalContents, publishedContents, draftContents, totalViews, totalComments, followerCount, followingCount);
        }
    }
}
