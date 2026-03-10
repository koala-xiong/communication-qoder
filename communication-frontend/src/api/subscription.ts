import http from './http'
import type { Content, PageResponse } from './content'

interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

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
  async subscribe(authorId: number): Promise<boolean> {
    try {
      await http.post<ApiResponse<void>>(`/subscriptions/${authorId}`)
      return true
    } catch (error) {
      return false
    }
  },

  async unsubscribe(authorId: number): Promise<boolean> {
    try {
      await http.delete<ApiResponse<void>>(`/subscriptions/${authorId}`)
      return true
    } catch (error) {
      return false
    }
  },

  async checkSubscription(authorId: number): Promise<boolean> {
    try {
      const response = await http.get<ApiResponse<boolean>>(`/subscriptions/check/${authorId}`)
      return response.data.data
    } catch (error) {
      return false
    }
  },

  async getMySubscriptions(page = 0, size = 20): Promise<PageResponse<User> | null> {
    try {
      const response = await http.get<ApiResponse<PageResponse<User>>>('/subscriptions/my', {
        params: { page, size }
      })
      return response.data.data
    } catch (error) {
      return null
    }
  },

  async getFollowers(userId: number, page = 0, size = 20): Promise<PageResponse<User> | null> {
    try {
      const response = await http.get<ApiResponse<PageResponse<User>>>(`/subscriptions/followers/${userId}`, {
        params: { page, size }
      })
      return response.data.data
    } catch (error) {
      return null
    }
  },

  async getSubscriptionFeed(page = 0, size = 10): Promise<PageResponse<Content> | null> {
    try {
      const response = await http.get<ApiResponse<PageResponse<Content>>>('/subscriptions/feed', {
        params: { page, size }
      })
      return response.data.data
    } catch (error) {
      return null
    }
  },

  async getSubscriptionCount(userId: number): Promise<SubscriptionCount | null> {
    try {
      const response = await http.get<ApiResponse<SubscriptionCount>>(`/subscriptions/count/${userId}`)
      return response.data.data
    } catch (error) {
      return null
    }
  }
}
