import http from './http'
import type { Content, PageResponse, ContentStatus } from './content'

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

export interface User {
  id: number
  username: string
  email?: string
  avatarUrl?: string
  bio?: string
}

export const dashboardApi = {
  getStats() {
    return http.get<DashboardStats>('/dashboard/stats')
  },

  getMyContents(status?: ContentStatus, page = 0, size = 10) {
    return http.get<PageResponse<Content>>('/dashboard/contents', {
      params: { status, page, size }
    })
  },

  updateProfile(data: UpdateProfileRequest) {
    return http.put<User>('/dashboard/profile', data)
  },

  uploadAvatar(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return http.post<User>('/dashboard/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
