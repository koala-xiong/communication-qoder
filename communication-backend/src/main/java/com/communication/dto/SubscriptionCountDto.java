package com.communication.dto;

public class SubscriptionCountDto {
    private long subscriptions;
    private long followers;

    public SubscriptionCountDto() {}

    public SubscriptionCountDto(long subscriptions, long followers) {
        this.subscriptions = subscriptions;
        this.followers = followers;
    }

    public long getSubscriptions() { return subscriptions; }
    public void setSubscriptions(long subscriptions) { this.subscriptions = subscriptions; }
    public long getFollowers() { return followers; }
    public void setFollowers(long followers) { this.followers = followers; }
}
