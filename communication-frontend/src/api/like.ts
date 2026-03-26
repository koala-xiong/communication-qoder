import http from './http'
import type { ApiResponse } from './auth'
import type { Content, PageResponse } from './content'

export const likeApi = {
  toggle(contentId: number) {
    return http.post<ApiResponse<{ liked: boolean }>>(`/likes/${contentId}`)
  },

  check(contentId: number) {
    return http.get<ApiResponse<boolean>>(`/likes/check/${contentId}`)
  },

  batchCheck(contentIds: number[]) {
    return http.post<ApiResponse<Record<string, boolean>>>(`/likes/batch-check`, contentIds)
  },

  getMyLiked(page = 0, size = 10) {
    return http.get<ApiResponse<PageResponse<Content>>>(`/likes/my`, {
      params: { page, size }
    })
  }
}
