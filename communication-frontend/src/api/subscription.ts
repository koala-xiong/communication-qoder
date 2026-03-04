import http from './http'
import type { Content, PageResponse } from './content'

export interface User {
  id: number
  username: string
  avatarUrl?: string
  bio?: string
}

export interface SubscriptionCount {
  subscriptions: number
  followers: number
}

export const subscriptionApi = {
  subscribe(authorId: number) {
    return http.post<void>(`/subscriptions/${authorId}`)
  },

  unsubscribe(authorId: number) {
    return http.delete<void>(`/subscriptions/${authorId}`)
  },

  checkSubscription(authorId: number) {
    return http.get<boolean>(`/subscriptions/check/${authorId}`)
  },

  getMySubscriptions(page = 0, size = 20) {
    return http.get<PageResponse<User>>('/subscriptions/my', {
      params: { page, size }
    })
  },

  getFollowers(userId: number, page = 0, size = 20) {
    return http.get<PageResponse<User>>(`/subscriptions/followers/${userId}`, {
      params: { page, size }
    })
  },

  getSubscriptionFeed(page = 0, size = 10) {
    return http.get<PageResponse<Content>>('/subscriptions/feed', {
      params: { page, size }
    })
  },

  getSubscriptionCount(userId: number) {
    return http.get<SubscriptionCount>(`/subscriptions/count/${userId}`)
  }
}
