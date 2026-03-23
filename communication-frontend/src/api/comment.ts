import http from './http'
import type { ApiResponse } from './auth'

export interface Author {
  id: number
  username: string
  avatarUrl?: string
}

export interface Comment {
  id: number
  contentId: number
  user: Author
  body: string
  parentId?: number
  replies?: Comment[]
  createdAt: string
  updatedAt: string
}

export interface CreateCommentRequest {
  body: string
  parentId?: number
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

export const commentApi = {
  getComments(contentId: number, page = 0, size = 20) {
    return http.get<ApiResponse<PageResponse<Comment>>>(`/contents/${contentId}/comments`, {
      params: { page, size }
    })
  },

  createComment(contentId: number, data: CreateCommentRequest) {
    return http.post<ApiResponse<Comment>>(`/contents/${contentId}/comments`, data)
  },

  deleteComment(contentId: number, commentId: number) {
    return http.delete<void>(`/contents/${contentId}/comments/${commentId}`)
  }
}
