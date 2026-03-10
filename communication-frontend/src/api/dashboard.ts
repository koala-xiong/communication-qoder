import http from './http'
import type { Content, PageResponse, ContentStatus } from './content'
import type { User } from './auth'

export interface DashboardStats {
  totalContents: number
  publishedContents: number
  draftContents: number
  totalViews: number
  totalComments: number
  followerCount: number
  followingCount: number
}

export interface UpdateProfileRequest {
  bio?: string
  avatarUrl?: string
}

interface ApiResponse<T> {
  data: T
}

export const dashboardApi = {
  async getStats(): Promise<DashboardStats | null> {
    try {
      const response = await http.get<ApiResponse<DashboardStats>>('/dashboard/stats')
      return response.data.data
    } catch (error) {
      return null
    }
  },

  async getMyContents(status?: ContentStatus, page = 0, size = 10): Promise<PageResponse<Content> | null> {
    try {
      const response = await http.get<ApiResponse<PageResponse<Content>>>('/dashboard/contents', {
        params: { status, page, size }
      })
      return response.data.data
    } catch (error) {
      return null
    }
  },

  async updateProfile(data: UpdateProfileRequest): Promise<User | null> {
    try {
      const response = await http.put<ApiResponse<User>>('/dashboard/profile', data)
      return response.data.data
    } catch (error) {
      return null
    }
  },

  async uploadAvatar(file: File): Promise<User | null> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      const response = await http.post<ApiResponse<User>>('/dashboard/avatar', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      return response.data.data
    } catch (error) {
      return null
    }
  }
}
