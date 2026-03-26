import http from './http'
import type { ApiResponse } from './auth'

export type MediaType = 'TEXT' | 'IMAGE' | 'VIDEO'
export type ContentStatus = 'DRAFT' | 'PUBLISHED'

export interface Author {
  id: number
  username: string
  email: string
  avatarUrl: string | null
  bio: string | null
}

export interface Content {
  id: number
  title: string
  body: string | null
  mediaUrl: string | null
  mediaType: MediaType
  viewCount: number
  commentCount: number
  likeCount: number
  status: ContentStatus
  categoryId?: number | null
  categoryName?: string | null
  tags?: string[]
  createdAt: string
  updatedAt: string
  author: Author
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface CreateContentRequest {
  title: string
  body?: string
  mediaUrl?: string
  mediaType?: MediaType
  status?: ContentStatus
  categoryId?: number | null
  tags?: string[]
}

export interface UpdateContentRequest {
  title?: string
  body?: string
  mediaUrl?: string
  mediaType?: MediaType
  status?: ContentStatus
  categoryId?: number | null
  clearCategory?: boolean
  tags?: string[]
}

export interface UploadResponse {
  url: string
  mediaType: MediaType
}

export const contentApi = {
  getContents(page = 0, size = 10) {
    return http.get<ApiResponse<PageResponse<Content>>>('/contents', {
      params: { page, size }
    })
  },

  getContentById(id: number) {
    return http.get<ApiResponse<Content>>(`/contents/${id}`)
  },

  getContentsByAuthor(authorId: number, page = 0, size = 10) {
    return http.get<ApiResponse<PageResponse<Content>>>(`/contents/user/${authorId}`, {
      params: { page, size }
    })
  },

  getMyContents(status?: ContentStatus, page = 0, size = 10) {
    return http.get<ApiResponse<PageResponse<Content>>>('/contents/my', {
      params: { status, page, size }
    })
  },

  createContent(data: CreateContentRequest) {
    return http.post<ApiResponse<Content>>('/contents', data)
  },

  updateContent(id: number, data: UpdateContentRequest) {
    return http.put<ApiResponse<Content>>(`/contents/${id}`, data)
  },

  deleteContent(id: number) {
    return http.delete<ApiResponse<void>>(`/contents/${id}`)
  },

  uploadImage(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return http.post<ApiResponse<UploadResponse>>('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  uploadVideo(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return http.post<ApiResponse<UploadResponse>>('/upload/video', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
