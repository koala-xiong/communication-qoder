import http from './http'
import type { ApiResponse } from './auth'
import type { Content, PageResponse } from './content'

export const favoriteApi = {
  toggle(contentId: number) {
    return http.post<ApiResponse<{ favorited: boolean }>>(`/favorites/${contentId}`)
  },

  check(contentId: number) {
    return http.get<ApiResponse<boolean>>(`/favorites/check/${contentId}`)
  },

  batchCheck(contentIds: number[]) {
    return http.post<ApiResponse<Record<string, boolean>>>(`/favorites/batch-check`, contentIds)
  },

  getMyFavorites(page = 0, size = 10) {
    return http.get<ApiResponse<PageResponse<Content>>>(`/favorites/my`, {
      params: { page, size }
    })
  },

  getCount() {
    return http.get<ApiResponse<number>>(`/favorites/count`)
  }
}
