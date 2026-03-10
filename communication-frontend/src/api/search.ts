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

export const searchApi = {
  async searchContents(q?: string, tag?: string, page = 0, size = 10): Promise<PageResponse<Content> | null> {
    try {
      const response = await http.get<ApiResponse<PageResponse<Content>>>('/search/contents', {
        params: { q, tag, page, size }
      })
      return response.data.data
    } catch (error) {
      return null
    }
  },

  async searchUsers(q: string, page = 0, size = 10): Promise<PageResponse<User> | null> {
    try {
      const response = await http.get<ApiResponse<PageResponse<User>>>('/search/users', {
        params: { q, page, size }
      })
      return response.data.data
    } catch (error) {
      return null
    }
  },

  async getPopularTags(limit = 20): Promise<string[]> {
    try {
      const response = await http.get<ApiResponse<string[]>>('/search/tags/popular', {
        params: { limit }
      })
      return response.data.data || []
    } catch (error) {
      return []
    }
  },

  async suggestTags(q: string): Promise<string[]> {
    try {
      const response = await http.get<ApiResponse<string[]>>('/search/tags/suggest', {
        params: { q }
      })
      return response.data.data || []
    } catch (error) {
      return []
    }
  }
}
