import http from './http'
import type { ApiResponse } from './auth'
import type { Content, PageResponse } from './content'

export const trendingApi = {
  list(page = 0, size = 10) {
    return http.get<ApiResponse<PageResponse<Content>>>(`/trending`, {
      params: { page, size }
    })
  },

  weekly(page = 0, size = 10) {
    return http.get<ApiResponse<PageResponse<Content>>>(`/trending/weekly`, {
      params: { page, size }
    })
  }
}
